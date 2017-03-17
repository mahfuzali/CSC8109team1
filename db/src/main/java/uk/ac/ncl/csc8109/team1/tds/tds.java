package uk.ac.ncl.csc8109.team1.tds;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.model.FairExchangeStage;
import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.model.LogEntity;
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.LogRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.LogRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;
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
	static LogRepository lr = new LogRepositoryImpl();
	static LogEntity le = new LogEntity();
	static FileRepository fr = new FileRepositoryImpl();
	//get time
	static long time = System.currentTimeMillis();

	/**
	 * Register new TDS user
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
		//send name to client
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		//create queue
		boolean success = sqsx.create(ClientName);
		System.out.println("Create client queue: " + success);
		String queue2Name = "csc8109_1_tds_queue_20070306_reg";
		boolean b = sqsx.registerResponse(queue2Name, id, ClientName);
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
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
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

		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		// TDS message queue name
		final String tdsMessageQueue = "csc8109_1_tds_queue_20070306";

		// TDS registration queue name
		final String tdsRegistrationQueue = "csc8109_1_tds_queue_20070306_reg";

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
							userid = attributes.get("Userid").getStringValue();
							System.out.println("Userid=" + userid);
						}
						
						// Read public key
						String publickey = null;
						if(attributes.get("PublicKey") != null) 
						{
							publickey = attributes.get("PublicKey").getStringValue();
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

					// get the from id
					if(attributes.get("Source")!= null)
					{
						String fromid = attributes.get("Source").getStringValue().trim();  
						System.out.println(fromid);
						// get the toId
						if(attributes.get("Target")!= null)
						{
							String toId = attributes.get("Target").getStringValue().trim();
							System.out.println(toId);
							//get the message body
							if(message.getBody() != null) 
							{
								String Message = message.getBody();
								//queueName	  
								if(tdsMessageQueue != null) 
								{
									String queueName = tdsMessageQueue; 
									//get label and protocol and stage from db
									String table_label = fe.getUuid();
									String tabel_protocol = fe.getProtocol();
									int stage = fe.getStage();
									//read protocol
									if(attributes.get("Protocol") != null) 
									{

										String protocol = attributes.get("Protocol").getStringValue();
										System.out.println(protocol);
										if(tabel_protocol == null)
										{
											System.out.println("begin exchange");
											exchangeRequest(fromid, toId, Message, queueName, protocol);
											// Delete the message from the queue once it has been processed
											boolean a = sqsx.deleteMessage(tdsMessageQueue, messageHandle);  
											System.out.println("delete exchange request: " + a);
										}
										//read label from message
										if(attributes.get("Label") != null) {
											String Label = attributes.get("Label").getStringValue();
											System.out.println(Label);
											if (table_label == Label)
											{
												if(tabel_protocol==protocol) 
												{
													System.out.println("bigin CoffeySaidha 2");
													boolean success = CoffeySaidha.runStep(Label, stage, message, fromid,toId);
													System.out.println(success);
													// Delete the message from the queue once it has been processed
													sqsx.deleteMessage(tdsMessageQueue, messageHandle);             		   
												}
											}
										}


										if (attributes.get("Protocol").equals(null)) {  

											if(attributes.get("Label") != null) 
											{
												String Label = attributes.get("Label").getStringValue();
												System.out.println(Label);
												String table_protocol = fe.getProtocol();
												if(table_label == Label) 
												{
													if(table_protocol == "CoffeySaidha") 
													{
														System.out.println("begin CoffeySaidha");
														boolean success = CoffeySaidha.runStep(Label, stage, message, fromid,toId);
														System.out.println(success);
														// Delete the message from the queue once it has been processed
														sqsx.deleteMessage(tdsMessageQueue, messageHandle);
													}
												}
											}
											if(attributes.get("Label").equals(null))
												System.out.println("protocol and label are both null");

										}

									}

								}

							}

						}

					}

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

