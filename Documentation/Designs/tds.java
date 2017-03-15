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
    	System.out.println(id);
		// Create a message queue name
	    String ClientName = "ClientName" + UUID.randomUUID().toString();	    
	    //send name to client
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		//create queue
	    boolean success = sqsx.create(ClientName);
		String queue2Name = "csc8109_1_tds_queue_20070306_reg";
		boolean b = sqsx.registerResponse(queue2Name, id, ClientName);
		System.out.println(b);
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
	 * step 1
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
		String fromId_queue = rr.getQueueById(fromId);
		String label = fe.getUuid();
		String fromid = fe.getFromID();
		String toid = fe.getToID();
		String toid_publickey = rr.getPublicKeyById(toid);
		System.out.println(toid_publickey);
		boolean b = sqsx.exchangeResponse(fromId_queue, label, message, fromid, toid, toid_publickey);
		System.out.println(b);
		if(!b){
			throw new IllegalArgumentException("send message error");
		}
		
		if(fe!=null){

			fe.setTimestamp(time);
			fe.setLastMessage(message);
			fe.setSenderqueue(fromId_queue);
		    fe.setStage(2);
			mr.storeMessage(uuid, fe);
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
		  if (message!=null) {			
			  messageHandle = message.getReceiptHandle();
			  Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
		      // Read message
			  //read user id
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
		  messagePollCount = 0;
		  }
		        		
		// Poll for normal message
	    Map<String, MessageAttributeValue> attributes = null;	    
	    messageHandle = null;
        message = sqsx.receiveMessage(tdsMessageQueue);
        ByteBuffer document;
	        if (message != null) {
	        	System.out.println("begin");
	        	messageHandle = message.getReceiptHandle();
	            attributes = message.getMessageAttributes();
          //	check state of exchange from database matching label
             int stage = fe.getStage();
             String table_label = fe.getUuid();
             String tabel_protocol = fe.getProtocol();
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
                      		if(tabel_protocol == null) {
                            	System.out.println("begin exchange");
                            	exchangeRequest(fromid, toId, Message, queueName, protocol);
                            	
                            if (protocol.equals(null)) {
                            	if(table_label == Label) {
                            		if(tabel_protocol == "CoffeySaidha"){
                            			 boolean success = CoffeySaidha.runStep(Label, stage, message, fromid, toId,queueName);
                            			 (Message message, String label, String fromid, String fromQueue, String fromPK, String toid, String toQueue
                            			 // Delete the message from the queue once it has been processed
                                         sqsx.deleteMessage(tdsMessageQueue, messageHandle);
                            		}
                            		
                            	}
                            }
                            
                           if (table_label == Label){
                        	   if(tabel_protocol==protocol) {
                        		   boolean success = CoffeySaidha.runStep(Label, stage, message, fromid, toId,queueName);
                        		// Delete the message from the queue once it has been processed
                                   sqsx.deleteMessage(tdsMessageQueue, messageHandle);             		   
                        	   }
                           }
                        	   
                            }
                            }
            		  }
            		  }
            	}
             }
             }
            

            
            // if protocol null
            // read the label from message
            // lookup the label in fe table
            // get protocol name from fe table
            // if protocol == CoffeySaidha
            // get step number from fe table
            
	        }
             messagePollCount++;
             Thread.sleep(1000);
		  }
		  }
             catch (InterruptedException e) {
         		running = false;
	    }


    }
	}	
}
	
