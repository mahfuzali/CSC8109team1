package uk.ac.ncl.csc8109.team1.tds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;

public class tds {
	
	static UUID uuid;
	static FairExchangeEntity fe = new FairExchangeEntity();
	static RegisterRepository rr = new RegisterRepositoryImpl();
	//static RegisterEntity re = new RegisterEntity();
	static MessageRepository mr = new MessageRepositoryImpl();
	/**
	 * step 0
	 */
	public static void registerUser(){
		//initialization, use interface
		
		
		//register alice and bob
		
		RegisterEntity reg_Alice = new RegisterEntity();
		RegisterEntity reg_Bob = new RegisterEntity();
//		
		//get their keys
		String Alice_id = "alice000";
		String Bob_id = "bob000";
		
		//registration
		if(!rr.checkAlreadyExist(Alice_id)){
			reg_Alice.setId("alice000");
			reg_Alice.setPublicKey("alicepublickey000");
		}else{
			System.out.println("please change Alice's id!");
		}
		// string alice_id= alice123
		//检查在不在
		//RegisterEntity reg_Alice = new RegisterEntity("alice123", "alicepublickey");
		//RegisterEntity reg_Bob = new RegisterEntity("bob123", "bobpublickey");
		//id and publickey 是不是一个人
		if(!rr.checkAlreadyExist(Bob_id)){
			reg_Bob.setId("bob000");
			reg_Bob.setPublicKey("bobpublickey000");
			//RegisterEntity reg_Bob = new RegisterEntity("bob000", "bobpublickey000");
		}else{
			System.out.println("please change Bob's id!");
		}
		
		System.out.println("test 1");
		System.out.println(reg_Bob.getId());
		System.out.println(reg_Alice.getPublicKey());
		//System.out.println(rr.registerUser(re));
		
	}
	
	
	
	/**
	 * step 1
	 * @param Alice_id, Bob_id
	 * Alice send request 
	 */
	public static void getAliceBobKey(String Alice_id, String Bob_id){
//		initialization
//		RegisterRepository rr;
//		rr = new RegisterRepositoryImpl();
		
		
		
		//get public key by their id
//		String Alice_key = rr.getPublicKeyById(Alice_id);
//		String Bob_key = rr.getPublicKeyById(Bob_id);
		
		//check  user id 
		System.out.println(rr.getPublicKeyById("alice000"));
		boolean Alice_Exist = rr.checkAlreadyExist(Alice_id);
		boolean Bob_Exist = rr.checkAlreadyExist(Bob_id);
		System.out.println(Alice_Exist);
		System.out.println(Bob_Exist);
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		//message log uuid,
//		Bob_id,
//		Alice_id,
//		Alice_key,
//		Bob_key,
//		"Request",
//		tt
		
		
		//store message in DB
		if((Alice_Exist==true) && (Bob_Exist==true)){
	     //	if(Bob_id!=null && Bob_key!=null){
				fe.setUuid(uuid);
		
				fe.setCreateTime(tt);
				fe.setFromID(Alice_id);
				fe.setToID(Bob_id);
				fe.setOriginHash("request");
				mr.storeMessage(uuid, fe);
			}
				else{
				System.out.println("Step 1(Bob) Error!");
			}
		 System.out.println("step1 test");
		 System.out.println(mr.getMessage(uuid));
		}
		
	//}
	
	/**
	 * step 2
	 * @param label
	 * send label to Alice
	 */
	public static void sendLabelToAlice(FairExchangeEntity fe){
		// generate label
		
		
		
		//send label to Alice
		
		
		
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		fe.setOriginHash("uuid");
		fe.setCreateTime(tt);
		
		if(fe!=null)
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 2 Error!");
		
	}
	
	/**
	 * step 3
	 * Alice send doc, eoo, L to TDS
	 * eoo--siga(h(doc))--publickey
	 */
	public static void receiveEOOFromAlice(FairExchangeEntity fe, String publickey, String label3, String Alice_id, String doc){
		
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		RegisterRepository rr;
		rr = new RegisterRepositoryImpl();
		
		FileRepository fr;
		fr = new FileRepositoryImpl();
		
		//get the public key 
		String Alice_key = rr.getPublicKeyById(Alice_id);
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		fe.setOriginHash("publickey");
		fe.setCreateTime(tt);

		FileEntity fileEntity = new FileEntity();
		File initialFile = new File("src/main/resources/sample.txt");
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            Assert.fail();
        }
        fileEntity.setFileName(initialFile.getName());
        fileEntity.setInputStream(targetStream);
        String key =UUID.randomUUID().toString();
		
        if (Alice_key == publickey){
		mr.storeMessage(uuid, fe);
        fr.storeFile(key, fileEntity);
        }
        else{ System.out.println("Step 2 Error!"); }
        	

	}
	
	/**
	 * step 4
	 * send EOO and lable to BOb
	 * eoo--publick key
	 */
	public static void sendEOOToBob(FairExchangeEntity fe){
		
		
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		//get message from last step
	
		//send message(eoo) and label to BOb
		
		String step3message = fe.getOriginHash();
				
		
		
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
				
		fe.setCreateTime(tt);
				
		if(fe!=null)
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 4 Error!");
				
	}
	
	/**
	 * step 5
	 * receive EOR from Bob
	 * EOR=Bobpublic key
	 */
	public static void receiveEORFromBob(FairExchangeEntity fe, String Bob_id, String Bob_publickey, String label){
		//receive message form Bob
		
		//get Bob public key
		RegisterRepository rr;
		rr = new RegisterRepositoryImpl();
		String Bob_key = rr.getPublicKeyById(Bob_id);
		
		
	 	//store message 	
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
				
		fe.setOriginHash("EOR");
		fe.setCreateTime(tt);
		
		//check label and public key
		if(label == uuid.toString() && Bob_key == Bob_publickey)
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 5 Error!");
				
	}
	
	/**
	 * step 6
	 * send doc to Bob
	 */
	public static void sendDocToBob(FairExchangeEntity fe){
		 
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		FileRepository fr = new FileRepositoryImpl();
		FileEntity f = fr.getFile(uuid.toString());
		//send doc to Bob
		
		
		
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		fe.setOriginHash("doc");
		fe.setCreateTime(tt);
		
		
		if(fe!=null)
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 6 Error!");
		
	}
	
	/**
	 * step 7
	 * @param label
	 * send EOR,label to alice
	 */
	public static void sendEORtoAlice(FairExchangeEntity fe){
	   //get the EOR
		String step5message = fe.getOriginHash(); 
		// send EOR and label to ALice
		
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		fe.setOriginHash("EOR");
		fe.setCreateTime(tt);
		
		if(fe!=null)
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 7 Error!");
		
	}
	
	/**
	 * step 8
	 * @param Alice_label
	 * @param Bob_label
	 */
	public static void receiveBothLabel(String Alice_label, String Bob_label,FairExchangeEntity fe){
		// alice and bob send label to tds
		
		
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
				
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
				
		fe.setOriginHash("EOR");
		fe.setCreateTime(tt);
				
		if(fe!=null && Alice_label == uuid.toString() && Bob_label == uuid.toString())
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 8 Error!");
				
	}
	
	public static void main(String[] args) {
		System.out.println("Hello world");
		//registerUser();
		registerUser();
		getAliceBobKey("alice000","bob000");
	}
	
	
}
