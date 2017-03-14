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
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.tds.CoffeySaidha;

/**
 * Created by Yue on 2017/3/2.
 */
public class tds {



	static UUID uuid;
	static FairExchangeEntity fe = new FairExchangeEntity();
	static RegisterRepository rr = new RegisterRepositoryImpl();
	static RegisterEntity re = new RegisterEntity();
	static MessageRepository mr = new MessageRepositoryImpl();
	static FileRepository fr = new FileRepositoryImpl();
	private static CryptoInterface crypto;
	//get time
	static long time = System.currentTimeMillis();
			
	/**
	 * step 0
	 */

	public static void register(String id, String publickey) {
		//check id
		if(rr.checkAlreadyExist(id)){
			throw new IllegalArgumentException("user id already exists");
		}
		
		// Create a message queue name
	    String ClientName = "ClientName" + UUID.randomUUID().toString();
	    //send name to client
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String queue2Name = "csc8109_1_tds_queue_20070306";
		boolean b = sqsx.registerResponse(queue2Name, id, ClientName);
		if(!b){
			throw new IllegalArgumentException("send queue error");
		}

		//store message
		re.setId(id);
		re.setPublicKey(publickey);
		re.setQueueName(ClientName);
		rr.registerUser(re);
	}
	/**
	 * step 1
	 * @param Alice_id, Bob_id
	 * Alice send request 
	 */
	public static void step1(String fromId, String toId, String message, String queueName,String protocol){

        //check id
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
	
		// create UUId
		uuid = UUID.randomUUID();
		//store message
		fe.setTimestamp(time);
		fe.setFromID(fromId);
		fe.setToID(toId);
		fe.setUuid(uuid.toString());
		fe.setStage(1);
		fe.setProtocol(protocol);
		fe.setLastMessage(message);
		fe.setReceiverqueue(queueName);
		mr.storeMessage(uuid,fe);
	
	/**
	 * step 2
	 * @param fe
	 * send label to Alice
	 */
		//send message
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String queue2Name = "csc8109_1_tds_queue_20070306";
		String label = fe.getUuid();
		String fromid = fe.getFromID();
		String toid = fe.getToID();
		boolean b = sqsx.sendMessage(queue2Name, label, message, fromid, toid);
		if(!b){
			throw new IllegalArgumentException("send message error");
		}
		
		if(fe!=null){

			fe.setTimestamp(time);
			fe.setLastMessage(message);
			fe.setSenderqueue(queue2Name);
		    fe.setStage(2);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 2 Error!");
		
	}
	
	public static void main(String[] args) {
		System.out.println("Hello world");
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
				
		// TDS message queue name
		final String tdsMessageQueue = "csc8109_1_tds_queue_20070306";
			    
		// TDS registration queue name
		final String tdsRegistrationQueue = "csc8109_1_tds_queue_20070306_reg";
			    
		  String messageHandle = null;
		  Message message = null;
		        
		  // Count how many message poll attempts we have made in between registration poll attempts
		  int messagePollCount = 0;

		      // Message polling
		  boolean running = true;
		  while(running) {
		  try {
		  // If we have made 10 message poll attempts since the last registration poll attempt
		  if (messagePollCount==10) {
		  // Check for a registration message
		  message = sqsx.receiveMessage(tdsRegistrationQueue);
		  if (message!=null) {
			  messageHandle = message.getReceiptHandle();
			  Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
		      // Read message
//			  String queueName = queueName;
			  String publickey = message.getBody();
	          String userid = attributes.get("Userid").getStringValue();
		      // Register user
	          register(userid, publickey);
		      // Delete message from the queue
		      sqsx.deleteMessage(tdsRegistrationQueue, messageHandle);
		  }
		  messagePollCount = 0;
		  }
		        		
		// Poll for normal message
	    Map<String, MessageAttributeValue> attributes = null;
	    
	    messageHandle = null;
        message = sqsx.receiveMessage(tdsMessageQueue);
        ByteBuffer document;
	        if (message != null) {
	        	messageHandle = message.getReceiptHandle();
	            attributes = message.getMessageAttributes();
	       // If the message is a request for an exchange, it will have a non-null protocol attribute
            String protocol = attributes.get("Protocol").getStringValue();
          //	get exchange label
            String label = attributes.get("Label").getStringValue();
          //	check state of exchange from database matching label
             int stage = fe.getStage();
             String tabel_protocol = fe.getProtocol();
           // get the from id
            String fromid = attributes.get("Source").getStringValue();  
           // get the toId
            String toId = attributes.get("Target").getStringValue();
           //get the message body
            String Message = message.getBody();
            //queueName
            String queueName = tdsMessageQueue;

            
//	            read protocol, step from state
             if (label.equals(uuid) && tabel_protocol == protocol) {
            	 
            	 boolean success = CoffeySaidha.runStep(label, stage, message, fromid, toId,queueName);
                 
                 // Delete the message from the queue once it has been processed
                 sqsx.deleteMessage(tdsMessageQueue, messageHandle);
           	 

             }
	        }
             messagePollCount++;
             Thread.sleep(1000);
		  }
             catch (InterruptedException e) {
         		running = false;
	    }


    }
	}	
	
}
