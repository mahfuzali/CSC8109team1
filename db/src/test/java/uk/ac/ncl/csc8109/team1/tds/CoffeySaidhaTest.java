package uk.ac.ncl.csc8109.team1.tds;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class CoffeySaidhaTest {
	
	MessageInterface sqsx;
	String TDS_QueueName;
	String source;
	String target;
	String sourceQueueName;
	String targetQueueName;
	CryptoInterface sourceCrypto;
	CryptoInterface targetCrypto;
	String sourcePublicKey;
	String targetPublicKey;
    String label;
    Message message;
    RegisterRepository userRegistry;
    RegisterEntity user;
    MessageRepository stateRepository;
	
	@Before
	public void setup() {
		sqsx = new AmazonExtendedSQS("csc8109team1");
		label = "e401ee10-e2ff-437f-ab0e-ce2038681d98";
		TDS_QueueName = "junit_test_csc8109_1_tds_queue";
		source = "Alice";
		target = "Bob";
		sourceQueueName = "junit_test_csc8109_1_tds_queue_20070306_alice";
		targetQueueName = "junit_test_csc8109_1_tds_queue_20070306_bob";
		sourceCrypto = new Crypto();
		targetCrypto = new Crypto();
		sourcePublicKey = sourceCrypto.getPublicKey();
		targetPublicKey = targetCrypto.getPublicKey();
		userRegistry = new RegisterRepositoryImpl();
		user = new RegisterEntity();
		user.setId(source);
		user.setPublicKey(sourcePublicKey);
		user.setQueueName(sourceQueueName);
		userRegistry.registerUser(user);
		user.setId(target);
		user.setPublicKey(targetPublicKey);
		user.setQueueName(targetQueueName);
		userRegistry.registerUser(user);
		stateRepository = new MessageRepositoryImpl();
		FairExchangeEntity stateEntity = new FairExchangeEntity();
		UUID uuidLabel = UUID.fromString(label);;
		long timestamp = System.currentTimeMillis();
		stateEntity.setUuid(label);
		stateEntity.setTimestamp(timestamp);
		stateEntity.setStage(0);
		stateEntity.setFromID(source);
		stateEntity.setToID(target);
		stateEntity.setSenderqueue(sourceQueueName);
		stateEntity.setReceiverqueue(targetQueueName);
		stateEntity.setLastMessage("");
		stateRepository.storeMessage(uuidLabel, stateEntity);
	}

	@Test
	public void testRunStep1() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		File docFile = new File(filename);
		String hash = sourceCrypto.getHashOfFile(docFile);
		String msgEOO = sourceCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.runStep(label, 1, message, source, target);       
		assertTrue(result);
	}
	
	@Test
	public void testRunStep1FakeEOO() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate fake EOO message (using Bob's key)
		File docFile = new File(filename);
		String hash = targetCrypto.getHashOfFile(docFile);
		String msgEOO = targetCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.runStep(label, 2, message, source, target);       
		assertFalse(result);
	}

	@Test
	public void testRunStep2() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		File docFile = new File(filename);
		String hash = sourceCrypto.getHashOfFile(docFile);
		String msgEOO = sourceCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 1, message, source, target);
		
		// Generate EOR message
		String msgEOR = targetCrypto.getSignature(msgEOO);
		
		// Send EOR message to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgEOR, target, source);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.runStep(label, 2, message, target, source);       
		assertTrue(result);
	}
	
	@Test
	public void testRunStep2WrongSig() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		File docFile = new File(filename);
		String hash = sourceCrypto.getHashOfFile(docFile);
		String msgEOO = sourceCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 1, message, source, target);
		
		// Generate EOR message
		// This time, Alice has signed the EOO again instead of Bob
		String msgEOR = sourceCrypto.getSignature(msgEOO);
		
		// Send EOR message to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgEOR, target, source);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.runStep(label, 2, message, target, source);       
		assertFalse(result);
	}
	
	@Test
	public void testRunStep3_and_4() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		File docFile = new File(filename);
		String hash = sourceCrypto.getHashOfFile(docFile);
		String msgEOO = sourceCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 1, message, source, target);
		
		// Generate EOR message
		String msgEOR = targetCrypto.getSignature(msgEOO);
		
		// Send EOR message to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgEOR, target, source);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 2, message, target, source);
		
		// Generate label message from Alice
		String msgLabel = sourceCrypto.getSignature(label.replaceAll("-",""));
		
		// Send label message from Alice to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgLabel, source, target);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
        CoffeySaidha.runStep(label, 3, message, source, target);
        
		// Generate label message from Bob
		msgLabel = targetCrypto.getSignature(label.replaceAll("-",""));
		
		// Send label message from Bob to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgLabel, target, source);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
        boolean result = CoffeySaidha.runStep(label, 4, message, target, source);
        
		assertTrue(result);
	}
	
	@Test
	public void testRunStep3_and_4_bothAlice() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		File docFile = new File(filename);
		String hash = sourceCrypto.getHashOfFile(docFile);
		String msgEOO = sourceCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 1, message, source, target);
		
		// Generate EOR message
		String msgEOR = targetCrypto.getSignature(msgEOO);
		
		// Send EOR message to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgEOR, target, source);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 2, message, target, source);
		
		// Generate label message from Alice
		String msgLabel = sourceCrypto.getSignature(label.replaceAll("-",""));
		
		// Send label message from Alice to TDS
		sqsx.sendMessage(TDS_QueueName, label, msgLabel, source, target);
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
        CoffeySaidha.runStep(label, 3, message, source, target);
        
		// Send another label message from Alice
	
        boolean result = CoffeySaidha.runStep(label, 4, message, source, target);
        
		assertFalse(result);
	}
	
	@Test
	public void testAbortStep1() {	
		// Generate Abort message
		String msgAbort = sourceCrypto.getSignature("AbortRequest");
		
        // Send an abort message with attached document
		sqsx.abortRequest(TDS_QueueName, label, msgAbort, source, target);   
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.abortExchange(label, 1, message, source);
		assertTrue(result);
	}
	
	@Test
	public void testAbortStep2() {
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		File docFile = new File(filename);
		String hash = sourceCrypto.getHashOfFile(docFile);
		String msgEOO = sourceCrypto.getSignature(hash);
		
        // Send a message with attached document
        sqsx.sendMsgDocument(TDS_QueueName, label, msgEOO, filename, source, target);       
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		CoffeySaidha.runStep(label, 1, message, source, target);  
		
		
		// Generate Abort message
		String msgAbort = sourceCrypto.getSignature("AbortRequest");
		
        // Send an abort message with attached document
		sqsx.abortRequest(TDS_QueueName, label, msgAbort, source, target);   
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.abortExchange(label, 2, message, source);
		assertTrue(result);
	}
	
	@Test
	public void testAbortStep3() {	
		// Generate Abort message
		String msgAbort = sourceCrypto.getSignature("AbortRequest");
		
        // Send an abort message with attached document
		sqsx.abortRequest(TDS_QueueName, label, msgAbort, source, target);   
        // Receive message back from queue
        message = sqsx.receiveMessage(TDS_QueueName);
        if (message != null) {
            sqsx.deleteMessage(TDS_QueueName, message.getReceiptHandle());
        }
		
		boolean result = CoffeySaidha.abortExchange(label, 3, message, source);
		assertFalse(result);
	}
}
