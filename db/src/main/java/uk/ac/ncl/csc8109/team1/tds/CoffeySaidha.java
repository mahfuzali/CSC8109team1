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

import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class CoffeySaidha {
	
	/**
	 * Source sends document and EOO to TDS
	 * EOO = SigA(h(doc))
	 * @param message
	 * @param label
	 * @param fromid
	 * @param fromQueue
	 * @param fromPK
	 */
	public static boolean sendDocEOO(Message message, String label, String fromid, String fromQueue, String fromPK){

		// Read message with attached document
        String msgEOO = message.getBody();
        Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
        String docName = attributes.get("DocumentName").getStringValue();
        ByteBuffer document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
        document.flip();
        
        // Download document
        OutputStream outputFile;
        WritableByteChannel outputChannel = null;
		try {
			outputFile = new FileOutputStream(docName);
            outputChannel = Channels.newChannel(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
        try {
			outputChannel.write(document);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
        System.out.println("Document " + docName + " received and downloaded");

		// Check EOO
        CryptoInterface crypto = new Crypto();
		File f = new File(docName);
		try {
			String hash = crypto.getHashOfFile(f);
			String verification = crypto.isVerified(hash, fromPK, msgEOO);
			System.out.println("EOO verification:" + verification);
			if(!verification.equals("Verified")){
				System.err.println("EOO not verified");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Upload document
        FileRepository fileRepository = new FileRepositoryImpl();
		FileEntity fileEntity = new FileEntity();
		File initialFile = new File(docName);
		InputStream targetStream = null;
		try {
			targetStream = new FileInputStream(initialFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		fileEntity.setFileName(initialFile.getName());
		fileEntity.setInputStream(targetStream);
		String fileKey = UUID.randomUUID().toString();
		try {
			fileRepository.storeFile(fileKey, fileEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Document " + fileEntity.getFileName() + " uploaded to S3 as " + fileKey);
//
//		if (label == uuid.toString())  {  
//			//get time
//			long time = System.currentTimeMillis();
//			fe.setTimestamp(time);
//			fe.setLastMessage(message);
//			fe.setStage(3);
//			mr.storeMessage(uuid, fe);
			
//
//		}
		
		return true;

	}
	
	/**
	 * step 4
	 * send EOO and lable to BOb
	 * eoo--publick key
	 */
	public static void step4(){


//		//get message(EOO) from last step
//		String message = fe.getLastMessage();
//		String label = fe.getUuid();
//		String fromid = fe.getFromID();
//		String toid = fe.getToID();
//		//send message(eoo) and label to BOb
//		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
//		String queueName = "csc8109_1_tds_queue_20070306";
//		boolean b = sqsx.sendMessage(queueName, label, message, fromid, toid);
//
//
//
//
//		if(fe!=null) {
//			//get time
//			long time = System.currentTimeMillis();
//			fe.setTimestamp(time);
//			fe.setLastMessage(message);
//			fe.setSenderqueue(queueName);
//			fe.setStage(4);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 4 Error!");

	}

	/**
	 * step 5
	 * receive EOR from Bob
	 * EOR=Bobpublic key = sigb(siga(hash(doc)))
	 */
	public static void step5(String toId, String fromId, String message, String label, String queuename, String protocol){
//		//check id 
//		if(!rr.checkAlreadyExist(fromId)){
//			throw new IllegalArgumentException("fromuser id not exists");
//		}
//		if(!rr.checkAlreadyExist(toId)){
//			throw new IllegalArgumentException("touser id not exists");
//		}
//
//		//check eor
//		String eoo = fe.getLastMessage();
//		String fromId_publickey = rr.getPublicKeyById(fromId); 
//		String verification = crypto.isVerified(eoo, fromId_publickey, message);
//		if(!verification.equals("if successful")){
//			throw new IllegalArgumentException("touser id not exists");
//		}
//
//		//check label and public key
//		if(label == uuid.toString()) {
//			//get time
//			long time = System.currentTimeMillis();
//			fe.setTimestamp(time);
//			fe.setLastMessage(message);
//			fe.setStage(5);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 5 Error!");

	}

	/**
	 * step 6
	 * send doc to Bob
	 */
	public static void step6(){

//		//get the doc
//		FileEntity f = fr.getFile(uuid.toString());
//		//send doc to Bob
//		//send message
//		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
//		String queueName = "csc8109_1_tds_queue_20070306";
//		String label = fe.getUuid();
//		String message = fe.getLastMessage();
//		String fromid = fe.getFromID();
//		String toid = fe.getToID();
//		boolean b = sqsx.sendMsgDocument(queueName, label, message, "f", fromid, toid);
//
//		if(fe!=null) {
//			//get time
//			long time = System.currentTimeMillis();
//			fe.setTimestamp(time);
//			fe.setLastMessage(message);
//			fe.setStage(6);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 6 Error!");

	}

	/**
	 * step 7
	 * @param fe
	 * send EOR,label to alice
	 */
	public static void step7(){
//		//get the EOR
//		//get message(EOO) from last step
//		String message = fe.getLastMessage();
//		String label = fe.getUuid();
//		String fromid = fe.getFromID();
//		String toid = fe.getToID();
//		//send message(eoo) and label to BOb
//		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
//		String queueName = "csc8109_1_tds_queue_20070306";
//		boolean b = sqsx.sendMessage(queueName, label, message, fromid, toid);
//
//
//		if(fe!=null) {
//			//get time
//			long time = System.currentTimeMillis();
//			fe.setTimestamp(time);
//			fe.setLastMessage(message);
//			fe.setSenderqueue(queueName);
//			fe.setStage(7);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 7 Error!");

	}

	/**
	 * step 8
	 * @param Alice_label
	 * @param Bob_label
	 */
	public static void step8(String fromId, String toId, String message, String label, String queueName, String protocol){
		// receive alice and bob send label to tds


//		// //check id 
//		if(!rr.checkAlreadyExist(fromId)){
//			throw new IllegalArgumentException("fromuser id not exists");
//		}
//		if(!rr.checkAlreadyExist(toId)){
//			throw new IllegalArgumentException("touser id not exists");
//		}
//		if(fe!=null && message == uuid.toString() && message == uuid.toString()) {
//			//get time
//			long time = System.currentTimeMillis();
//			fe.setTimestamp(time);
//			fe.setLastMessage("label");
//			fe.setStage(8);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 8 Error!");

	}

	/**
	 * Abort an exchange
	 * @param label
	 * @param step
	 * @param message
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean abortExchange(String label, int step, Message message, String source, String target) {
		return true;
	}
	
	/**
	 * Run requested step of the Coffey Saidha fair exchange protocol
	 * @param label
	 * @param step
	 * @param message
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean runStep(String label, int step, Message message, String source, String target) {
		
		// String sourceQueue, String targetQueue, String sourcePK, String targetPK
		RegisterRepository userRegistry = new RegisterRepositoryImpl();
		
		// Get public key, queue names for source and target
		String sourcePK = userRegistry.getPublicKeyById(source);
		if (sourcePK == null) {
			System.err.println("Can't find public key for user " + source);
			return false;
		}
		String sourceQueue = userRegistry.getQueueById(source);
		if (sourceQueue == null) {
			System.err.println("Can't find queue name for user " + source);
			return false;
		}
		String targetPK = userRegistry.getPublicKeyById(target);
		if (targetPK == null) {
			System.err.println("Can't find public key for user " + target);
			return false;
		}
		String targetQueue = userRegistry.getQueueById(target);
		if (targetQueue == null) {
			System.err.println("Can't find queue name for user " + target);
			return false;
		}
		
		switch (step)
		{ 
		case 2:
			if (!sendDocEOO(message, label, source, sourceQueue, sourcePK)) {
				return false;
			};
			break;

		case 3:
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
		
		return true;
	}	
}
