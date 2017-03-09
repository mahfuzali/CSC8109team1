package uk.ac.ncl.csc8109.team1.tds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



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

	public static void register(String id, String publickey) {
		//RegisterRepository registerRepository = new RegisterRepositoryImpl();
		RegisterEntity registerEntity = new RegisterEntity();
		if(rr.checkAlreadyExist(id)){
			throw new IllegalArgumentException("user id already exists");
		}
		registerEntity.setId(id);
		registerEntity.setPublicKey(publickey);
		rr.registerUser(registerEntity);


		//todo 加密部分

	}
	public static void registerUser(){
		
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
		
		
		//get public key by their id
//		String Alice_key = rr.getPublicKeyById(Alice_id);
//		String Bob_key = rr.getPublicKeyById(Bob_id);
		
		//check  user id 
		System.out.println(rr.getPublicKeyById("alice000"));
		boolean Alice_Exist = rr.checkAlreadyExist(Alice_id);
		boolean Bob_Exist = rr.checkAlreadyExist(Bob_id);
		System.out.println(Alice_Exist);
		System.out.println(Bob_Exist);
		
		
		
		//store message in DB
		if((Alice_Exist==true) && (Bob_Exist==true)){
	     //	if(Bob_id!=null && Bob_key!=null){
				fe.setUuid(uuid);
				fe.setFromID(Alice_id);
				fe.setToID(Bob_id);
				fe.setLastMessage("label");
				fe.setStage(1);
				mr.storeMessage(uuid, fe);
			}
				else{
				System.out.println("Step 1(Bob) Error!");
			}
		 System.out.println("step1 test");
		 System.out.println(mr.getMessage(uuid));
		}

	public UUID storeMeg(String fromId, String toId){


		//RegisterEntity registerEntity = new RegisterEntity();
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		//check sig
		FairExchangeEntity fairExchangeEntity = new FairExchangeEntity();
		fairExchangeEntity.setFromID(fromId);
		fairExchangeEntity.setToID(toId);
		uuid = UUID.randomUUID();
		fairExchangeEntity.setUuid(uuid);
		fairExchangeEntity.setStage(1);
		mr.storeMessage(uuid,fairExchangeEntity);


		return uuid;
	}
		
	//}
	
	/**
	 * step 2
	 * @param fe
	 * send label to Alice
	 */
	public static void sendLabelToAlice(FairExchangeEntity fe){
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
	public static void receiveEOOFromAlice(FairExchangeEntity fe, String publickey, String label3, String Alice_id, String doc){
		
		
		//get the public key 
		String Alice_key = rr.getPublicKeyById(Alice_id);
		

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
		
        if (Alice_key == publickey){
        fe.setLastMessage("eoo");
        fe.setStage(3);
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
	public static void receiveEORFromBob(FairExchangeEntity fe, String Bob_id, String Bob_publickey, String label){
		//receive message form Bob
		
		
		//get Bob public key
		String Bob_key = rr.getPublicKeyById(Bob_id);
		
		
		//check label and public key
		if(label == uuid.toString() && Bob_key == Bob_publickey) {
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
	public static void sendDocToBob(FairExchangeEntity fe){
		 
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
	public static void sendEORtoAlice(FairExchangeEntity fe){
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
	public static void receiveBothLabel(String Alice_label, String Bob_label,FairExchangeEntity fe){
		// receive alice and bob send label to tds
		
		
	
		if(fe!=null && Alice_label == uuid.toString() && Bob_label == uuid.toString()) {
			fe.setLastMessage("label");
		    fe.setStage(8);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 8 Error!");
				
	}
	
	public static void main(String[] args) {
		System.out.println("Hello world");
//		registerUser();
//		getAliceBobKey("alice000","bob000");
	}
	
	
}
