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
import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class CoffeySaidha {
	
    private static FileRepository fileRepository = new FileRepositoryImpl();
	private static MessageRepository stateRepository = new MessageRepositoryImpl();
	private static MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

	/**
	 * Alice (source) sends document and EOO to TDS
	 * EOO = SigA(h(doc))
	 * @param message
	 * @param label
	 * @param fromid
	 * @param fromQueue
	 * @param fromPK
	 * @param toid
	 * @param toQueue
	 * @return
	 */
	public static boolean sendDocEOO(Message message, String label, String fromid, String fromQueue, String fromPK, String toid, String toQueue){
		
		// Check for valid exchange
		FairExchangeEntity stateEntity = null;
		UUID uuidLabel = null;
		try {
			uuidLabel = UUID.fromString(label);
			stateEntity = stateRepository.getMessage(uuidLabel);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

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
		
		// Update state table
		long timestamp = System.currentTimeMillis();
		stateEntity.setTimestamp(timestamp);
		stateEntity.setStage(2);
		stateEntity.setFromID(fromid);
		stateEntity.setToID(toid);
		stateEntity.setSenderqueue(fromQueue);
		stateEntity.setReceiverqueue(toQueue);
		stateEntity.setLastMessage(msgEOO);
		stateEntity.setFileKey(fileKey);
		try {
			stateRepository.storeMessage(uuidLabel, stateEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Updated state table for exchange " + label);
		
		// Send EOO to Target
		if (!sqsx.sendMsgSourceKey(toQueue, label, msgEOO, fromid, toid, fromPK)) {
			System.err.println("Can't send message to queue " + toQueue);
			return false;			
		};
		System.out.println("Sent EOO to queue " + toQueue);
		
		return true;

	}

	/**
	 * Bob (source) sends EOR to TDS and TDS sends it to Alice (target)
	 * EOR = SigB(SigA(h(doc)))
	 * @param message
	 * @param label
	 * @param fromid
	 * @param fromQueue
	 * @param fromPK
	 * @param toid
	 * @param toQueue
	 * @return
	 */
	public static boolean exchangeEOR(Message message, String label, String fromid, String fromQueue, String fromPK, String toid, String toQueue){

		// Check for valid exchange
		FairExchangeEntity stateEntity = null;
		UUID uuidLabel = null;
		try {
			uuidLabel = UUID.fromString(label);
			stateEntity = stateRepository.getMessage(uuidLabel);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// Get EOO from last message
		String msgEOO = stateEntity.getLastMessage();
		
		// Get EOR from current message
        String msgEOR = message.getBody();
		
		// Check EOR
        CryptoInterface crypto = new Crypto();
		try {
			String verification = crypto.isVerified(msgEOO, fromPK, msgEOR);
			System.out.println("EOR verification:" + verification);
			if(!verification.equals("Verified")){
				System.err.println("EOR not verified");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Update state table
		long timestamp = System.currentTimeMillis();
		stateEntity.setTimestamp(timestamp);
		stateEntity.setStage(3);
		stateEntity.setFromID(fromid);
		stateEntity.setToID(toid);
		stateEntity.setSenderqueue(fromQueue);
		stateEntity.setReceiverqueue(toQueue);
		stateEntity.setLastMessage(msgEOR);
		try {
			stateRepository.storeMessage(uuidLabel, stateEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Updated state table for exchange " + label);
		
		// Send EOO to Target
		if (!sqsx.sendMessage(toQueue, label, msgEOO, fromid, toid)) {
			System.err.println("Can't send message to queue " + toQueue);
			return false;			
		};
		System.out.println("Sent EOR to queue " + toQueue);
		
		return true;
	}
	
//	/**
//	 * step 6
//	 * send doc to Bob
//	 */
////		//get the doc
//		FileEntity f = fr.getFile(uuid.toString());
//		//send doc to Bob
//		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
//		String queueName = "csc8109_1_tds_queue_20070306";
//		String ClientqueueName = rr.getQueueById(toId);
//		String fromid = fe.getFromID();
//		String toid = fe.getToID();
//		boolean b = sqsx.sendMsgDocument(ClientqueueName, label, "f", "f", fromid, toid);
//		if(!b){
//			throw new IllegalArgumentException("send message error");
//		}
//
//		if(fe!=null) {
//			fe.setTimestamp(time);
//			fe.setLastMessage("f");
//			fe.setStage(6);
//			fe.setSenderqueue(ClientqueueName);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 6 Error!");
//
//	/**
//	 * step 7
//	 * @param fe
//	 * send EOR,label to alice
//	 */
//
//		//send Eor to alice
//		String ClientqueueName2 = rr.getQueueById(fromId);
//		boolean c = sqsx.sendMessage(ClientqueueName2, label, eor, fromid, toid);
//		if(!c){
//			throw new IllegalArgumentException("send message error");
//		}
//
//		if(fe!=null) {
//			fe.setTimestamp(time);
//			fe.setLastMessage(eor);
//			fe.setSenderqueue(ClientqueueName2);
//			fe.setStage(7);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 7 Error!");
//		
//		return true;
//
//	}
//
//	/**
//	 * step 8
//	 * @param Alice_label
//	 * @param Bob_label
//	 */
//	public static boolean step8(String label, Message message, String fromId, String toId, String FromqueueName){
//		// //check id 
//		if(!rr.checkAlreadyExist(fromId)){
//			throw new IllegalArgumentException("fromuser id not exists");
//		}
//		if(!rr.checkAlreadyExist(toId)){
//			throw new IllegalArgumentException("touser id not exists");
//		}
//		//check message
//		String messsage2 = message.getBody();
//		if(fe!=null && messsage2 == uuid.toString()) {
//			fe.setTimestamp(time);
//			fe.setLastMessage("label");
//			fe.setStage(8);
//			mr.storeMessage(uuid, fe);
//		}
//		else
//			System.out.println("Step 8 Error!");
//		
//		return true;
//	}

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
		case 1:
			if (!sendDocEOO(message, label, source, sourceQueue, sourcePK, target, targetQueue)) {
				return false;
			};
			break;

		case 2:
			if (!exchangeEOR(message, label, source, sourceQueue, sourcePK, target, targetQueue)) {
				return false;
			};
			break;
			
		case 4:
//			step5(source, target, Message, label, queueName, protocol);
			//               	 case stage=5:
			//step6();
			//               	 case stage=6:
			//step7();
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
