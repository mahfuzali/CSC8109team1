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
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class CoffeySaidha {
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
	 * step3
	 * Source sends document and EOO to TDS
	 * EOO = SigA(h(doc))
	 * @param message
	 * @param label
	 * @param fromid
	 * @param fromQueue
	 * @param fromPK
	 * @return 
	 */
	public static boolean step3(String label,Message message,  String fromid, String toId,String FromqueueName){

		// Check EOO
        CryptoInterface crypto = new Crypto();
        String Eoo = message.getBody();
		File f = new File(docName);
		String fromPK = rr.getPublicKeyById(fromid);
		String hash = crypto.getHashOfFile(f);
		String verification = crypto.isVerified(hash, fromPK, Eoo);
		System.out.println("EOO verification:" + verification);
		if(!verification.equals("Verified")){
			System.err.println("EOO not verified");
			return false;
		}
		
		// Read message with attached document
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
		String key =UUID.randomUUID().toString();

		if (label == uuid.toString())  {  
			fe.setTimestamp(time);
			fe.setLastMessage(Eoo);
			fe.setFromID(fromid);
			fe.setToID(toId);
			fe.setReceiverqueue(FromqueueName);
			fe.setStage(3);
			mr.storeMessage(uuid, fe);
			fr.storeFile(key, fileEntity);

		}
		
	/**
	 * step 4
	 * send EOO and lable to BOb
	 * eoo--publick key
	 */

		//send message(eoo) and label to BOb
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String queueName = "csc8109_1_tds_queue_20070306";
		String ClientqueueName = rr.getQueueById(toId);
		boolean b = sqsx.sendMessage(ClientqueueName, label, Eoo, fromid, toId);
		if(!b){
			throw new IllegalArgumentException("send message error");
		}

		if(fe!=null) {
			fe.setTimestamp(time);
			fe.setLastMessage(Eoo);
			fe.setSenderqueue(ClientqueueName);
			fe.setStage(4);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 4 Error!");
		return true;

	}

	/**
	 * step 5
	 * receive EOR from Bob
	 * EOR=Bobpublic key = sigb(siga(hash(doc)))
	 */
	public static boolean step5(String label,Message message, String fromId, String toId, String queuename){
//		//check id 
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}

		//check eor
		String eoo =fe.getLastMessage();
		String eor = message.getBody();
		String fromId_publickey = rr.getPublicKeyById(fromId); 
		String verification = crypto.isVerified(eoo, fromId_publickey, eor);
		if(!verification.equals("verified")){
			throw new IllegalArgumentException("eor is wrong!");
		}

		//check label and public key
		if(label == uuid.toString()) {
			fe.setTimestamp(time);
			fe.setLastMessage(eor);
			fe.setFromID(fromId);
			fe.setReceiverqueue(queuename);
			fe.setToID(toId);
			fe.setStage(5);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 5 Error!");

	/**
	 * step 6
	 * send doc to Bob
	 */
//		//get the doc
		FileEntity f = fr.getFile(uuid.toString());
		//send doc to Bob
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		String queueName = "csc8109_1_tds_queue_20070306";
		String ClientqueueName = rr.getQueueById(toId);
		String fromid = fe.getFromID();
		String toid = fe.getToID();
		boolean b = sqsx.sendMsgDocument(ClientqueueName, label, "f", "f", fromid, toid);
		if(!b){
			throw new IllegalArgumentException("send message error");
		}

		if(fe!=null) {
			fe.setTimestamp(time);
			fe.setLastMessage("f");
			fe.setStage(6);
			fe.setSenderqueue(ClientqueueName);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 6 Error!");

	/**
	 * step 7
	 * @param fe
	 * send EOR,label to alice
	 */

		//send Eor to alice
		String ClientqueueName2 = rr.getQueueById(fromId);
		boolean c = sqsx.sendMessage(ClientqueueName2, label, eor, fromid, toid);
		if(!c){
			throw new IllegalArgumentException("send message error");
		}

		if(fe!=null) {
			fe.setTimestamp(time);
			fe.setLastMessage(eor);
			fe.setSenderqueue(ClientqueueName2);
			fe.setStage(7);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 7 Error!");
		
		return true;

	}

	/**
	 * step 8
	 * @param Alice_label
	 * @param Bob_label
	 */
	public static boolean step8(String label, Message message, String fromId, String toId, String FromqueueName){
		// //check id 
		if(!rr.checkAlreadyExist(fromId)){
			throw new IllegalArgumentException("fromuser id not exists");
		}
		if(!rr.checkAlreadyExist(toId)){
			throw new IllegalArgumentException("touser id not exists");
		}
		//check message
		String messsage2 = message.getBody();
		if(fe!=null && messsage2 == uuid.toString()) {
			fe.setTimestamp(time);
			fe.setLastMessage("label");
			fe.setStage(8);
			mr.storeMessage(uuid, fe);
		}
		else
			System.out.println("Step 8 Error!");
		
		return true;
	}

	public static boolean runStep(String label, int step, Message message, String fromId, String toId,String FromqueueName) {
		
		// String sourceQueue, String targetQueue, String sourcePK, String targetPK
		RegisterRepository userRegistry = new RegisterRepositoryImpl();
		
		// Get public key, queue names for source and target
		String sourcePK = userRegistry.getPublicKeyById(fromId);
		if (sourcePK == null) {
			System.err.println("Can't find public key for user " + fromId);
			return false;
		}
		String sourceQueue = userRegistry.getQueueById(fromId);
		if (sourceQueue == null) {
			System.err.println("Can't find queue name for user " + fromId);
			return false;
		}
		String targetPK = userRegistry.getPublicKeyById(fromId);
		if (targetPK == null) {
			System.err.println("Can't find public key for user " + fromId);
			return false;
		}
		String targetQueue = userRegistry.getQueueById(fromId);
		if (targetQueue == null) {
			System.err.println("Can't find queue name for user " + fromId);
			return false;
		}
		
		switch (step)
		{ 
		case 2:
			if (!step3(label, message, fromId, toId,FromqueueName)) {
				return false;
			};
			break;
		case 4:
			if (!step5(label, message, fromId,toId,FromqueueName)) {
				return false;
			};
			break;
		case 7:
			if (step8(label, message, fromId, toId,FromqueueName)) {
				return false;
			};
		case 8:
			System.out.println("exchange finish");
		default:
			System.out.println("error");
		}
		
		return true;
	}	
}
