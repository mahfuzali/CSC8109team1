package uk.ac.ncl.csc8109.team1.tds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

public class tds {



	static UUID uuid;
	static FairExchangeEntity fe = new FairExchangeEntity();
	static RegisterRepository rr = new RegisterRepositoryImpl();
	//static RegisterEntity re = new RegisterEntity();
	static MessageRepository mr = new MessageRepositoryImpl();
	static FileRepository fr = new FileRepositoryImpl();
	
	/**
	 * step 0
	 */

	public static void register(String id, String publickey, String sig) {
		//RegisterRepository registerRepository = new RegisterRepositoryImpl();
		RegisterEntity registerEntity = new RegisterEntity();
		if(rr.checkAlreadyExist(id)){
			throw new IllegalArgumentException("user id already exists");
		}
		
		//
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
	public static UUID step1(String fromId, String toId, String sig){

        //¼ì²ésig
		//RegisterEntity registerEntity = new RegisterEntity();
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		//check sig
		fe.setFromID(fromId);
		fe.setToID(toId);
		uuid = UUID.randomUUID();
		fe.setUuid(uuid.toString());
		fe.setStage(1);
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
		// generate label
		
		
		
		//send label to Alice
		
		
		
		//get the current system timestamp
		
		if(fe!=null){
			fe.setLastMessage("label");
		    fe.setStage(2);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 2 Error!");
		
	}
	
	/**
	 * step 3
	 * Alice send doc, eoo, L to TDS
	 * eoo--siga(h(doc))--publickey
	 */
	public static void step3(String Eoo, String label, String fromId, String toId, String doc){
		
		//receive message
		
		//check id
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}

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
		
        //¼ì²éid ºÍ EOO
   
        fe.setLastMessage("eoo");
        fe.setStage(3);
		mr.storeMessage(uuid, fe);
        fr.storeFile(key, fileEntity);
        
		}

	
	/**
	 * step 4
	 * send EOO and lable to BOb
	 * eoo--publick key
	 */
	public static void step4(){
		
		
		//get message(EOO) from last step
	     String Eoo = fe.getLastMessage();
		
		//send message(eoo) and label to BOb
		
				
		
		
				
		if(fe!=null) {
			fe.getLastMessage();
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
	public static void step5(String toId, String fromId, String EOR, String label){
		//receive message form Bob
		
		
	   //check id 
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		
		//check eor and id
		
		//check label and public key
		if(label == uuid.toString()) {
			fe.setLastMessage("EOR");
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
		
		
		if(fe!=null) {
	        fe.setLastMessage("doc");
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
		String EOR = fe.getLastMessage(); 
		
		
		// send EOR and label to ALice
		
		
		if(fe!=null) {
			fe.setLastMessage("EOR");
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
	public static void step8(String fromId, String toId, String from_label, String to_label){
		// receive alice and bob send label to tds
		
		
	   // //check id 
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		if(fe!=null && from_label == uuid.toString() && to_label == uuid.toString()) {
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
	    String queueName = "csc8109_1_tds_queue_20070306";
	    
        // Receive message
        String messageHandle = null;
        Message message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }
	    
	    
		register("alice1", "alicekey", "siga");
        register("bob1", "bobkey","sigb");
        step1("alice1", "bob1", "message");
        step2();
        step3("eoo", "label", "alice1", "bob1", "doc");
        step4();
        step5("alice1", "bob1", "message", "label");
        step6();
        step7();
        step8("alice1", "bob1","alice_label", "bob_label");
    }
	
	
}
