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
 * Created by Yue on 2017/3/2.
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
	 * step 0
	 */

	public static void register(String id, String publickey) {

		//check id
		if(rr.checkAlreadyExist(id)){
			throw new IllegalArgumentException("user id already exists");
		}
    	System.out.println(id);
		// Create a message queue name
	    String ClientName = "queueName" + UUID.randomUUID().toString();	    
	    //send name to client
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		//create queue
	    boolean success = sqsx.create(ClientName);
	    System.out.println("careate client queue: " + success);
		String queue2Name = "csc8109_1_tds_queue_20070306_reg";
		boolean b = sqsx.registerResponse(queue2Name, id, ClientName);
		System.out.println("send client queue Name" + b);
		if(!b){
			throw new IllegalArgumentException("send queue error");
		}

		//store message
		re.setId(id);
		re.setPublicKey(publickey);
		re.setQueueName(ClientName);
		rr.registerUser(re);
		String id2 = re.getId();
		System.out.println(id2);
		System.out.println("regist successful");
	}
	/**
	 * exchange request
	 * @param Alice_id, Bob_id
	 * Alice send request 
	 */
	public static void exchangeRequest(String fromId, String toId, String message, String queueName,String protocol){
         System.out.println(fromId);
         System.out.println(toId);
        //check id
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
	
		// create UUId
		uuid = UUID.randomUUID();
		
	
	/**
	 * @param fe
	 * send label to Alice
	 */
		//send message
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String fromId_queue = rr.getQueueById(fromId);
		String label = uuid.toString().replaceAll("-",  "");
		message = "exchange response£» label";
		String toid_publickey = rr.getPublicKeyById(toId);
		System.out.println(toid_publickey);
		boolean b = sqsx.exchangeResponse(fromId_queue, label, message, fromId, toId, toid_publickey);
		System.out.println("send exchange response: " + b);
		if(!b){
			throw new IllegalArgumentException("send message error");
		}
		
		if(message!=null){
			//store message to mr
			fe.setTimestamp(time);
			fe.setFromID(fromId);
			fe.setToID(toId);
			fe.setUuid(label);
			fe.setStage(1);
			fe.setProtocol(protocol);
			fe.setReceiverqueue(queueName);
			fe.setLastMessage(message);
			fe.setSenderqueue(fromId_queue);
			mr.storeMessage(uuid, fe);
			System.out.println("store message to mr");
//			//store message to log
//			le.setFromID(fromId);
//			le.setLastMessage(message);
//			le.setProtocol(protocol);
//			le.setUuid(uuid.toString());
//			le.setReceiverqueue(queueName);
//			le.setTimestamp(time);
//			le.setToID(toId);
//			le.setSenderqueue(fromId_queue);
//			le.setStage(1);
//			lr.storeLog(le);
//			System.out.println("store message to log");
			System.out.println("request successful");
		}
		else
			System.out.println("Step 2 Error!");
		
		System.out.println("change sucessful");
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
		  int messagePollCount = 10;

		      // Message polling
		  boolean running = true;
		  while(running) {
		  try {
//		  // If we have made 10 message poll attempts since the last registration poll attempt
		  if (messagePollCount==10) {
		  // Check for a registration message
		  message = sqsx.receiveMessage(tdsRegistrationQueue);
		  System.out.println("receive registe message");
		  if (message!=null) {			
			  messageHandle = message.getReceiptHandle();
			  Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
		      // Read message
			  //read user id
			  System.out.println("begin registe");
			  if(attributes.get("userid")!= null) {
				  String userid = attributes.get("userid").getStringValue();
				  if(attributes.get("PublicKey") != null) {
		          		String publickey = attributes.get("PublicKey").getStringValue();
		          		 // Register user
		  	          register(userid, publickey);
		  		      // Delete message from the queue
		  		      sqsx.deleteMessage(tdsRegistrationQueue, messageHandle);		
			  }
		  }
		  }
		  messagePollCount = 0;
		  }
		        		
		// Poll for normal message
	    Map<String, MessageAttributeValue> attributes = null;	    
	    messageHandle = null;
        message = sqsx.receiveMessage(tdsMessageQueue);
	        if (message != null) {
	        	System.out.println("begin receive message");
	        	messageHandle = message.getReceiptHandle();
	            attributes = message.getMessageAttributes();
         
           // get the from id
             if(attributes.get("Source")!= null){
            String fromid = attributes.get("Source").getStringValue().trim();  
         // get the toId
            if(attributes.get("Target")!= null){
            	 String toId = attributes.get("Target").getStringValue().trim();
            	//get the message body
            	if(message.getBody() != null) {
            		  String Message = message.getBody();
            		//queueName	  
            		  if(tdsMessageQueue != null) {
            			  String queueName = tdsMessageQueue;
            			  //read protocol
            			  if(attributes.get("Protocol") != null) {
                      		String protocol = attributes.get("Protocol").getStringValue();
                      	 //read label from message
                      		if(attributes.get("Label") != null) {
                          		String Label = attributes.get("Label").getStringValue();
                      	            //exchange request
                                    String table_label = fe.getUuid();
                                    String tabel_protocol = fe.getProtocol();
                      		if(tabel_protocol == null)
                      		{
                            	System.out.println("begin exchange");
                            	exchangeRequest(fromid, toId, Message, queueName, protocol);
                      		}
                			int stage = fe.getStage();
                            if (protocol.equals(null)) {
                            	if(table_label == Label) {
                            		if(tabel_protocol == "CoffeySaidha") {
                            			System.out.println("begin CoffeySaidha");
                            			 boolean success = CoffeySaidha.runStep(Label, stage, message, fromid,toId);
                                         System.out.println(success);
                            			 // Delete the message from the queue once it has been processed
                                         sqsx.deleteMessage(tdsMessageQueue, messageHandle);
                            		}
                            		
                            	}
                            }
                            
                           if (table_label == Label){
                        	   if(tabel_protocol==protocol) {
                        		   System.out.println("bigin CoffeySaidha 2");
                        		   boolean success = CoffeySaidha.runStep(Label, stage, message, fromid,toId);
                        		   System.out.println(success);
                        		// Delete the message from the queue once it has been processed
                                   sqsx.deleteMessage(tdsMessageQueue, messageHandle);             		   
                        	   }
                           }
                           
                           if (protocol.equals(null)&&Label.equals(null)){
                        	   System.out.println("protocol and label are both null");
                           }
                          		}   
                            }
                            }
            		  }
            		  }
            	}
             }
          System.out.println(messagePollCount);
            // if protocol null
            // read the label from message
            // lookup the label in fe table
            // get protocol name from fe table
            // if protocol == CoffeySaidha
            // get step number from fe table
            
	       
             messagePollCount++;
             Thread.sleep(1000);
		  }
             catch (InterruptedException e) {
         		running = false;
	    }


    }
	}	
}
	
