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
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
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
	
	@Before
	public void setup() {
		sqsx = new AmazonExtendedSQS("csc8109team1");
		TDS_QueueName = "csc8109_1_tds_queue_coffeysaidha_test";
		source = "Alice";
		target = "Bob";
		sourceQueueName = "csc8109_1_tds_queue_20070306_alice";
		targetQueueName = "csc8109_1_tds_queue_20070306_bob";
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
	}

	@Test
	public void testRunStep1() {
		label = "e401ee10-e2ff-437f-ab0e-ce2038681d98";
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
		label = "e401ee10-e2ff-437f-ab0e-ce2038681d98";
		String filename = "src/main/resources/sample.txt";
		
		// Generate EOO message
		String msgEOO = "Fake EOO";
		
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
		label = "e401ee10-e2ff-437f-ab0e-ce2038681d98";
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
		label = "e401ee10-e2ff-437f-ab0e-ce2038681d98";
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
}
