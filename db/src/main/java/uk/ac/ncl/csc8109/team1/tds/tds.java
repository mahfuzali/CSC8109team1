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

/**
 * Created by Yue on 2017/3/2.
 */
public class tds {



	static UUID uuid;
	static FairExchangeEntity fe = new FairExchangeEntity();
	static RegisterRepository rr = new RegisterRepositoryImpl();
	//static RegisterEntity re = new RegisterEntity();
	static MessageRepository mr = new MessageRepositoryImpl();
	static FileRepository fr = new FileRepositoryImpl();
	private static CryptoInterface crypto;
	/**
	 * step 0
	 */

	public static void register(String id, String publickey) {
		//RegisterRepository registerRepository = new RegisterRepositoryImpl();
		RegisterEntity registerEntity = new RegisterEntity();
		if(rr.checkAlreadyExist(id)){
			throw new IllegalArgumentException("user id already exists");
		}
		

		registerEntity.setId(id);
		registerEntity.setPublicKey(publickey);
		rr.registerUser(registerEntity);


		
		//todo 

	}
	/**
	 * step 1
	 * @param Alice_id, Bob_id
	 * Alice send request 
	 */
	public static UUID step1(String fromId, String toId, String message, String queueName,String protocol){

        //check id
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		//check message
		
		
		//get time
		long time = System.currentTimeMillis();
		
		fe.setTimestamp(time);
		fe.setFromID(fromId);
		fe.setToID(toId);
		uuid = UUID.randomUUID();
		fe.setUuid(uuid.toString());
		fe.setStage(1);
		fe.setProtocol(protocol);
		fe.setLastMessage(message);
		fe.setReceiverqueue(queueName);
		mr.storeMessage(uuid,fe);


		return uuid;
	}
		
	//}
	
	/**
	 * step 2
	 * @param fe
	 * send label to Alice
	 */
	public static void step2(){
		
		//send message
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String queue2Name = "csc8109_1_tds_queue_20070306";
		String label = fe.getUuid();
		String message = fe.getLastMessage();
		String fromid = fe.getFromID();
		String toid = fe.getToID();
		boolean b = sqsx.sendMessage(queue2Name, label, message, fromid, toid);
		
		
		if(fe!=null){

			//get time
			long time = System.currentTimeMillis();
			fe.setTimestamp(time);
			fe.setLastMessage(message);
			fe.setSenderqueue(queue2Name);
		    fe.setStage(2);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 2 Error!");
		
	}
	
	/**
	 * step 3
	 * Alice send doc, eoo, L to TDS
	 * eoo--siga(h(doc))--publickey, doc
	 * @param protocol 
	 * @param queueName 
	 * @param toId 
	 * @param fromid 
	 * @param label 
	 * @param message 
	 */
	public static void step3(String message, String label, String fromid, String toId, String queueName, String protocol){
		
		//check id
		if(!rr.checkAlreadyExist(fromid)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		
		 //check eoo		
		File f = new File("src/main/resources/sample.txt");
        String fromid_publick = rr.getPublicKeyById(fromid); 
        String hash = crypto.getHashOfFile(f);
		String verification = crypto.isVerified(hash, fromid_publick, message);
		if(!verification.equals("if successful")){
			throw new IllegalArgumentException("touser id not exists");
		}
        
        
        // save doc
		FileEntity fileEntity = new FileEntity();
		File initialFile = new File("src/main/resources/sample.txt");
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {

        }
        fileEntity.setFileName(initialFile.getName());
        fileEntity.setInputStream(targetStream);
        String key =UUID.randomUUID().toString();
		
        
        
     if (label == uuid.toString())  {  
      //get time
		long time = System.currentTimeMillis();
		fe.setTimestamp(time);
        fe.setLastMessage(message);
        fe.setStage(3);
		mr.storeMessage(uuid, fe);
        fr.storeFile(key, fileEntity);
        
		}

	}
	/**
	 * step 4
	 * send EOO and lable to BOb
	 * eoo--publick key
	 */
	public static void step4(){
		
		
		//get message(EOO) from last step
	     String message = fe.getLastMessage();
	     String label = fe.getUuid();
		 String fromid = fe.getFromID();
		 String toid = fe.getToID();
		//send message(eoo) and label to BOb
	     MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
			String queueName = "csc8109_1_tds_queue_20070306";
			boolean b = sqsx.sendMessage(queueName, label, message, fromid, toid);
				
		
		
				
		if(fe!=null) {
			//get time
			long time = System.currentTimeMillis();
			fe.setTimestamp(time);
			fe.setLastMessage(message);
			fe.setSenderqueue(queueName);
		    fe.setStage(4);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 4 Error!");
				
	}
	
	/**
	 * step 5
	 * receive EOR from Bob
	 * EOR=Bobpublic key = sigb(siga(hash(doc)))
	 */
	public static void step5(String toId, String fromId, String message, String label, String queuename, String protocol){
	   //check id 
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		
		//check eor
	     String eoo = fe.getLastMessage();
	     String fromId_publickey = rr.getPublicKeyById(fromId); 
	 	String verification = crypto.isVerified(eoo, fromId_publickey, message);
		if(!verification.equals("if successful")){
			throw new IllegalArgumentException("touser id not exists");
		}
		
		//check label and public key
		if(label == uuid.toString()) {
			//get time
			long time = System.currentTimeMillis();
			fe.setTimestamp(time);
			fe.setLastMessage(message);
		    fe.setStage(5);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 5 Error!");
				
	}
	
	/**
	 * step 6
	 * send doc to Bob
	 */
	public static void step6(){
		 
		//get the doc
		FileEntity f = fr.getFile(uuid.toString());
		//send doc to Bob
		//send message
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String queueName = "csc8109_1_tds_queue_20070306";
		String label = fe.getUuid();
		String message = fe.getLastMessage();
		String fromid = fe.getFromID();
		String toid = fe.getToID();
		boolean b = sqsx.sendMsgDocument(queueName, label, message, "f", fromid, toid);
		
		if(fe!=null) {
			//get time
			long time = System.currentTimeMillis();
			fe.setTimestamp(time);
	        fe.setLastMessage(message);
	        fe.setStage(6);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 6 Error!");
		
	}
	
	/**
	 * step 7
	 * @param fe
	 * send EOR,label to alice
	 */
	public static void step7(){
	   //get the EOR
		//get message(EOO) from last step
	     String message = fe.getLastMessage();
	     String label = fe.getUuid();
		 String fromid = fe.getFromID();
		 String toid = fe.getToID();
		//send message(eoo) and label to BOb
	     MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
			String queueName = "csc8109_1_tds_queue_20070306";
			boolean b = sqsx.sendMessage(queueName, label, message, fromid, toid);
				
		
		if(fe!=null) {
			//get time
			long time = System.currentTimeMillis();
			fe.setTimestamp(time);
			fe.setLastMessage(message);
			fe.setSenderqueue(queueName);
		    fe.setStage(7);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 7 Error!");
		
	}
	
	/**
	 * step 8
	 * @param Alice_label
	 * @param Bob_label
	 */
	public static void step8(String fromId, String toId, String message, String label, String queueName, String protocol){
		// receive alice and bob send label to tds
		
		
	   // //check id 
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		if(fe!=null && message == uuid.toString() && message == uuid.toString()) {
			//get time
			long time = System.currentTimeMillis();
			fe.setTimestamp(time);
			fe.setLastMessage("label");
		    fe.setStage(8);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 8 Error!");
				
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
            //get doc
           document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
            document.flip();
            
            OutputStream outputFile;
            WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("src/main/resources/sample.txt");
	            outputChannel = Channels.newChannel(outputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				outputChannel.write(document);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  
             
//	            read protocol, step from state
             if (label.equals(uuid) && tabel_protocol == protocol) {
            	 
            	 switch (stage)
            	 { 
            	 case 0:
            	 step1(fromid, toId, Message, queueName, protocol);
//            	 step2();
            	 case 2:
                 step3(Message, label, fromid, toId, queueName, protocol);
            	 
//                 case stage=3:
                 step4();
                 case 4:
                 step5(toId, fromid, Message, label, queueName, protocol);
//               	 case stage=5:
               	 step6();
//               	 case stage=6:
                 step7();
               	 case 7:
                 step8(fromid, toId, Message, label, queueName,protocol);
                 case 8:
                 System.out.println("exchange finish");
                 default:
                System.out.println("error");
                 
                 // Delete the message from the queue once it has been processed
                 sqsx.deleteMessage(tdsMessageQueue, messageHandle);
            	 
            	 }
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
