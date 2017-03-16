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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
	 * TDS receives document and EOO from Alice
	 * TDS sends EOO to Bob
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
	public static boolean receiveDocEOO(Message message, String label, String fromid, String fromQueue, String fromPK, String toid, String toQueue){
		
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
	 * Bob (source) sends EOR to TDS
	 * TDS sends Doc to Bob and EOR to Alice
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
	public static boolean receiveEOR(Message message, String label, String fromid, String fromQueue, String fromPK, String toid, String toQueue){

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
		
		// Send Doc to Source
		String fileKey = stateEntity.getFileKey();
		FileEntity S3document = fileRepository.getFile(fileKey);
		
		// Download document from S3		
		InputStream docInputStream = S3document.getInputStream();
		String docFileName = S3document.getFileName();
		
		Path docDownload = Paths.get(docFileName);
		try {
			Files.copy(docInputStream, docDownload, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Downloaded document " + docDownload.toString() + " from S3");
		
		// Send document to source
        if (!sqsx.sendMsgDocument(fromQueue, label, "Document", docFileName, toid, fromid)) {
			System.err.println("Can't send document to queue " + fromQueue);
			return false;	
        };
        System.out.println("Sent document to queue " + fromQueue);
		
		// Send EOO to Target
		if (!sqsx.sendMessage(toQueue, label, msgEOO, fromid, toid)) {
			System.err.println("Can't send message to queue " + toQueue);
			return false;			
		};
		System.out.println("Sent EOR to queue " + toQueue);
		
		return true;
	}
	
	/**
	 * Receive labels from Alice and Bob at the end of the exchange
	 * Called on steps 3 & 4
	 * Message should be SigA(label) or SigB(label)
	 * @param step
	 * @param message
	 * @param label
	 * @param fromid
	 * @param fromPK
	 * @return
	 */
	public static boolean receiveLabel(int step, Message message, String label, String fromid, String fromPK, String toid) {
		
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
		
		// Get signed label from current message
        String msgLabel = message.getBody();
        System.out.println("Received label " + msgLabel + " from " + fromid);
		
		// Check that signed label matches the sender and their public key
        CryptoInterface crypto = new Crypto();
		try {
			String verification = crypto.isVerified(label.replaceAll("-",""), fromPK, msgLabel);
			System.out.println("Label verification:" + verification);
			if(!verification.equals("Verified")){
				System.err.println("Label not verified");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
			
		// If step 3, then update state to step 4, save the message and record who sent it
		if (step == 3) {
			// Update state table
			long timestamp = System.currentTimeMillis();
			stateEntity.setTimestamp(timestamp);
			stateEntity.setStage(4);
			stateEntity.setFromID(fromid);
			stateEntity.setToID(toid);
			stateEntity.setSenderqueue("");
			stateEntity.setReceiverqueue("");
			stateEntity.setLastMessage(msgLabel);
			try {
				stateRepository.storeMessage(uuidLabel, stateEntity);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			System.out.println("Updated state table for exchange " + label);
			
			return true;
		}
		
		// If at final step, then check that the current sender matches the recipient of the last step
		// i.e. if Alice sent the label at step 3, check that this label is from Bob
		// This is because Alice and Bob can send back labels in any order, but both must do so
		if (!fromid.equals(stateEntity.getToID())) {
			// Label not from previous recipient
			System.err.println("Label received from unexpected sender " + fromid + "(expected " + stateEntity.getToID() + ")");
			return false;
		}

		// Delete document from S3
		String fileKey = stateEntity.getFileKey();
		try {
			fileRepository.deleteFile(fileKey);
		} catch (Exception e) {
			// Not great if we can't delete the file but shouldn't end the exchange
			e.printStackTrace();
		}
		System.out.println("Deleted document " + fileKey + " from S3");
		
		// Remove entry from state table
		try {
			stateRepository.deleteMessage(uuidLabel);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Delete state table entry for exchange " + label);
		
		return true;
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
		case 1:
			if (!receiveDocEOO(message, label, source, sourceQueue, sourcePK, target, targetQueue)) {
				return false;
			};
			break;

		case 2:
			if (!receiveEOR(message, label, source, sourceQueue, sourcePK, target, targetQueue)) {
				return false;
			};
			break;
			
		case 3:
		case 4:
			if (!receiveLabel(step, message, label, source, sourcePK, target)) {
				return false;
			};
			break;

		default:
			System.err.println("Unknown step");
		}
		
		return true;
	}	
}
