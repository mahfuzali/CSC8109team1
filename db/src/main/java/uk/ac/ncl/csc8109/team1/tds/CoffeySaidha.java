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
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class CoffeySaidha {

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

	public static boolean runStep(String label, int step, Message message, String source, String target) {
		
		// String sourceQueue, String targetQueue, String sourcePK, String targetPK
		
		switch (step)
		{ 
		case 2:
			step3(Message, label, source, target, queueName, protocol);

			//                 case stage=3:
			step4();
		case 4:
//			step5(source, target, Message, label, queueName, protocol);
			//               	 case stage=5:
			step6();
			//               	 case stage=6:
			step7();
		case 7:
//			step8(source, target, Message, label, queueName,protocol);
		case 8:
			System.out.println("exchange finish");
		default:
			System.out.println("error");
		}
	}	
}
