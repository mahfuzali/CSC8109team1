package uk.ac.ncl.csc8109.team1.tds;

import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.tds.CoffeySaidha;

/**
 * Created by Yue on 2017/3/16.
 */
public class tds {

	static UUID uuid;
	static FairExchangeEntity fe = new FairExchangeEntity();
	static RegisterRepository rr = new RegisterRepositoryImpl();
	static RegisterEntity re = new RegisterEntity();
	static MessageRepository mr = new MessageRepositoryImpl();
	static FileRepository fr = new FileRepositoryImpl();
	
	private static final String tdsMessageQueue = "csc8109_1_tds_queue_20070306"; // TDS message queue name
	private static final String tdsRegistrationQueue = "csc8109_1_tds_queue_20070306_reg"; // TDS registration queue name
	private static final MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1"); // Initialise message queueing service

	/**
	 * Register new TDS user
	 * @param id
	 * @param publickey
	 * @return
	 */
	public static boolean register(String id, String publickey) {
		// Check id
		if(rr.checkAlreadyExist(id))
		{
			System.err.println("User id already exists");
			return false;
		}
		System.out.println(id);
		// Create a message queue name
		String ClientName = "queueName" + UUID.randomUUID().toString();	    
		// Create queue and send to client
		boolean success = sqsx.create(ClientName);
		System.out.println("Create client queue: " + success);
		boolean b = sqsx.registerResponse(tdsRegistrationQueue, id, ClientName);
		System.out.println("Send client queue Name" + b);
		if(!b)
		{
			System.err.println("Send queue error");
			return false;
		}

		// Store user in registry
		re.setId(id);
		re.setPublicKey(publickey);
		re.setQueueName(ClientName);
		rr.registerUser(re);
		System.out.println("Registration successful");
		
		return true;
	}
	
	/**
	 * Exchange request
	 * @param fromId "Alice"'s user id
	 * @param toId "Bob"'s user id
	 * @param message
	 * @param queueName
	 * @param protocol
	 */
	public static boolean exchangeRequest(String fromId, String toId,
			String message, String queueName,String protocol){
		System.out.println(fromId);
		System.out.println(toId);

		// Check user ids
		if(!rr.checkAlreadyExist(fromId))
		{
			System.err.println("From user id does not exist");
			return false;
		}
		if(!rr.checkAlreadyExist(toId))
		{
			System.err.println("To user id does not exist");
			return false;
		}

		// Create UUID
		uuid = UUID.randomUUID();

		// Send label to Alice
		String fromId_queue = rr.getQueueById(fromId);
		System.out.println(fromId_queue);
		String label = uuid.toString().replaceAll("-",  "");
		message = "exchange response£» " + label;
		String toid_publickey = rr.getPublicKeyById(toId);
		System.out.println(toid_publickey);
		boolean b = sqsx.exchangeResponse(fromId_queue, label, message, fromId, toId, toid_publickey);
		System.out.println("Send exchange response: " + b);
		if (!b)
		{
			System.err.println("send message error");
			return false;
		}

		// Initialise exchange state table
		long time = System.currentTimeMillis(); // Get time
		fe.setTimestamp(time);
		fe.setFromID(fromId);
		fe.setToID(toId);
		fe.setUuid(label);
		fe.setStage(1);
		fe.setProtocol(protocol);
		fe.setReceiverqueue(queueName);
		fe.setLastMessage(message);
		fe.setSenderqueue(fromId_queue);
		try {
			mr.storeMessage(uuid, fe);
			System.out.println("Initialised exchange state");
		} catch (Exception e) {
			System.err.println("Can't initialise exchange state");
			e.printStackTrace();
			return false;
		}

		System.out.println("Exchange request sucessful");
		return true;
	}


	public static void main(String[] args) {
		System.out.println("TDS Service started");

		String messageHandle = null;
		Message message = null;

		// Count how many message poll attempts we have made in between registration poll attempts
		int messagePollCount = 10;

		// Message polling
		boolean running = true;

		while(running) {

			try {
				// If we have made 10 message poll attempts since the last registration poll attempt
				if (messagePollCount==10) 
				{
					// Check for a registration message
					message = sqsx.receiveMyMessage(tdsRegistrationQueue,"TDSUSER");
					System.out.println("User registration request message poll");

					if (message!=null) 
					{			
						System.out.println("User registration request received");
						
						// Read message
						messageHandle = message.getReceiptHandle();
						Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();

						// Read user id
						String userid = null;
						if(attributes.get("Userid")!= null) 
						{
							userid = attributes.get("Userid").getStringValue().trim();
							System.out.println("Userid=" + userid);
						}
						
						// Read public key
						String publickey = null;
						if(attributes.get("PublicKey") != null) 
						{
							publickey = attributes.get("PublicKey").getStringValue().trim();
							System.out.println("PublicKey=" + publickey);
						}
						
						if (userid!=null && publickey!=null) {
							// Register user
							register(userid, publickey);
						}
								
						// Delete message from the queue
						boolean a = sqsx.deleteMessage(tdsRegistrationQueue, messageHandle);	
						if(a == true)
						{
							System.out.print("Delete message successful");
						}
					}
					messagePollCount = 0;
				}

				// Poll for normal message
				Map<String, MessageAttributeValue> attributes = null;	    
				messageHandle = null;
				message = sqsx.receiveMessage(tdsMessageQueue);
				System.out.println("Standard message poll");
				if (message != null) 
				{
					System.out.println("Message received");
					messageHandle = message.getReceiptHandle();
					attributes = message.getMessageAttributes();
					
					if (attributes.get("Source") == null || attributes.get("Target") == null) {
						System.err.println("Source and Target attributes required");
						// Delete the message from the queue once it has been processed
						boolean success = sqsx.deleteMessage(tdsMessageQueue, messageHandle);  
						System.out.println("Delete message: " + success);	
						continue;
					}

					// Get the from id
					String fromid = attributes.get("Source").getStringValue().trim();  
					System.out.println("Source=" + fromid);

					// Get the toId
					String toId = attributes.get("Target").getStringValue().trim();
					System.out.println("Target=" + toId);
					
					// Get the protocol if present
					String protocol = null;
					if (attributes.get("Protocol") != null) 
					{
						protocol = attributes.get("Protocol").getStringValue();
						System.out.println("Protocol=" + protocol);
					}
					
					// Get the label if present
					String Label = null;
					if (attributes.get("Label") != null) {
						Label = attributes.get("Label").getStringValue();
						System.out.println("Label=" + Label);
					}
					
					// Get the message body
					String Message = message.getBody();				

					// If the protocol was sent, this is a request for an exchange
					if (protocol != null)
					{
						System.out.println("Exchange requested");
						exchangeRequest(fromid, toId, Message, tdsMessageQueue, protocol);
						
					} else if (Label != null) {
						// If the protocol was null, and a label was sent, this is a normal exchange message
						
						// Lookup exchange from state table using the label
						UUID uuidLabel = null;
						try {
							uuidLabel = UUID.fromString(Label);
							fe = mr.getMessage(uuidLabel);
						} catch (Exception e) {
							System.err.println("Can't find exchange " + Label);
							e.printStackTrace();
							// Delete the message from the queue once it has been processed
							boolean success = sqsx.deleteMessage(tdsMessageQueue, messageHandle);  
							System.out.println("Delete message: " + success);	
							continue;
						}
						
						// Get the protocol and stage from the state table
						String table_protocol = fe.getProtocol();
						int stage = fe.getStage();
						
						if (table_protocol.equals("CoffeySaidha")) {
							System.out.println("CoffeySaidha step " + stage);
							boolean success = CoffeySaidha.runStep(Label, stage, message, fromid,toId);
							System.out.println(success);
						} else {
							System.err.println("Unrecognised protocol " + table_protocol);
						}
						
					} else {
						System.err.println("Unrecognised message: protocol and label are both null");
					}
					
					// Delete the message from the queue once it has been processed
					boolean success = sqsx.deleteMessage(tdsMessageQueue, messageHandle);  
					System.out.println("Delete message: " + success);				
				}

				// Update count of messages between registration requests and sleep for 1s
				System.out.println(messagePollCount);
				messagePollCount++;
				Thread.sleep(1000);
			}
			catch (InterruptedException e){
				running = false;
			}

		}

	}

}

