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

	/**
	 * step 0
	 */
	public static void registerUser(){
		//initialization, use interface
		RegisterRepository rr;
		rr = new RegisterRepositoryImpl();
		
		//register alice and bob
		RegisterEntity reg_Alice = new RegisterEntity("alice123", "alicepublickey");
		RegisterEntity reg_Bob = new RegisterEntity("bob123", "bobpublickey");
		
		//get their keys
		String Alice_id = reg_Alice.getId();
		String Bob_id = reg_Bob.getId();
		
		//registration
		if(!rr.checkAlreadyExist(Alice_id)){
			rr.registerUser(reg_Alice);
		}else{
			System.out.println("please change Alice's id!");
		}
		
		if(!rr.checkAlreadyExist(Bob_id)){
			rr.registerUser(reg_Bob);
		}else{
			System.out.println("please change Bob's id!");
		}
		
		System.out.println("test 1");
		
	}
	
	/**
	 * step 1
	 * @param Alice_id, Bob_id
	 */
	public static void getAliceBobKey(String Alice_id, String Bob_id){
//		initialization
		RegisterRepository rr;
		rr = new RegisterRepositoryImpl();
		
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		//get key by their id
		String Alice_key = rr.getPublicKeyById(Alice_id);
		String Bob_key = rr.getPublicKeyById(Bob_id);
		
		//get the current system timestamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		//message log
		FairExchangeEntity fe = 
				new FairExchangeEntity(uuid,
						Bob_id,
						Alice_id,
						Alice_key,
						Bob_key,
						"Request",
						tt);
		
		//store message in DB
		if(Alice_id!=null && Alice_key!=null){
			if(Bob_id!=null && Bob_key!=null){
				mr.storeMessage(uuid, fe);
			}else{
				System.out.println("Step 1(Bob) Error!");
			}
		}else{
			System.out.println("Step 1(Alice) Error!");
		}
		
	}
	
	/**
	 * step 2
	 * @param label
	 */
	public static void sendLabelToAlice(FairExchangeEntity fe){
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
	 */
	public static void receiveEOOFromAlice(FairExchangeEntity fe){
		
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		//FileRepository fr;
		//fr = new FileRepositoryImpl();
		
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		long tt = ts.getTime();
		
		fe.setOriginHash("eoo");
		fe.setCreateTime(tt);

		FileEntity fileEntity = new FileEntity();
        FileRepository fileRepository = new FileRepositoryImpl();
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
        fileRepository.storeFile(key,fileEntity);
		
		mr.storeMessage(uuid, fe);

	}
	
	/**
	 * step 4
	 */
	public static void sendEOOToBob(FairExchangeEntity fe){
		//send EOO and label to Bob
		
		
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
//		FairExchangeEntity fe2 = mr.getMessage(uuid);  这里有问题
		//send message(eoo) to BOb
				
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
	 */
	public static void receiveEORFromBob(FairExchangeEntity fe){
		//receive message form Bob
		
		
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
			System.out.println("Step 5 Error!");
				
	}
	
	/**
	 * step 6
	 */
	public static void sendDocToBob(doc, FairExchangeEntity fe){
		//send document to Bob
		
		
		 
		//initialization
		MessageRepository mr;
		mr = new MessageRepositoryImpl();
		
		FileRepository fr;
		FileEntity f = fr.getFile(uuid);
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
	 */
	public static void sendEORtoAlice(FairExchangeEntity fe){
//		send EOR to Alice
		
		
		
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
				
		if(fe!=null)
			mr.storeMessage(uuid, fe);
		else
			System.out.println("Step 8 Error!");
				
	}
	
	public static void main(String[] args) {
		registerUser();
	}
	
	s
}
