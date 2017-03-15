package uk.ac.ncl.csc8109.team1.message;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageApp;
import org.junit.Test;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

public class MessageTest {
	
	private static boolean sendReq;
	private static String queueName;
	private static String messageHandle = null;
	private static String recReq = null;
	private static boolean sendMsg;
	private static boolean success;
	private Message message;
	private String receivedMsg;
	private boolean sendDoc;
	
	// Initialise queue service
	private static MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
	
	
	
	@BeforeClass
	public static void setUp(){
		
		sendMsg = false;
		
		//Create a queue name
		queueName = "test3QueueName" + UUID.randomUUID().toString();
		
		// Create a queue
		sendMsg = sqsx.create(queueName);    
	}

	@Test
	public void testsendRegReqNotNull() {
		
		//send a registration Request
		sendReq = sqsx.registerRequest(queueName, "Alice", "PUBLICKEY");
		
		assertNotNull(sendReq);
	}
	
	@Test
	public void testRecReqNotNull(){
		
		//send a registration Request
		sendReq = sqsx.registerRequest(queueName, "Alice", "PUBLICKEY");
		
		// Receive registration request
        Message message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	recReq = message.getReceiptHandle();
        }
        
		// Delete message
        sendReq = sqsx.deleteMessage(queueName, recReq);

		assertNotNull(recReq);
		
			}
	
	@Test
	public void testSendMsgNotNull(){
		
		// Send a message
		sendMsg = sqsx.sendMessage(queueName, "Exchange #1", "Simple test message #1", "Alice", "Bob");
        
        assertNotNull(sendMsg);
	}
	
	@Test
	public void testRecMsgNotNull(){
        
        // Receive message
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        }
        
        // Delete message
        sendMsg = sqsx.deleteMessage(queueName, messageHandle);   
        assertNotNull(messageHandle);
	}
	
	@Test
	public void testSendDocNotNull(){
		
		 // Send a message with attached document
        sendDoc = sqsx.sendMsgDocument(queueName, "Exchange #1", "Simple test message #2", "src/main/resources/sample", "Alice", "Bob");
        
        assertNotNull(sendDoc);	
	}
	
	@Test
	public void testDocNotNull(){
		 // Send a message with attached document
        sendDoc = sqsx.sendMsgDocument(queueName, "Exchange #1", "Simple test message #2", "src/main/resources/sample", "Alice", "Bob");
        
        // Receive message with attached document
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        ByteBuffer document;
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
       
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
    
            document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
            document.flip();
            
            OutputStream outputFile;
            WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("src/main/resources/received");
	            outputChannel = Channels.newChannel(outputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				outputChannel.write(document);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            assertNotNull(document);
        }
	}
	
	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public static boolean isSendReq() {
		return sendReq;
	}

	public static void setSendReq(boolean sendReq) {
		MessageTest.sendReq = sendReq;
	}

	public String getMessageHandle() {
		return messageHandle;
	}

	public void setMessageHandle(String messageHandle) {
		this.messageHandle = messageHandle;
	}

	public boolean isSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(boolean sendMsg) {
		this.sendMsg = sendMsg;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getReceivedMsg() {
		return receivedMsg;
	}

	public void setReceivedMsg(String receivedMsg) {
		this.receivedMsg = receivedMsg;
	}

	public static String getRecReq() {
		return recReq;
	}

	public static void setRecReq(String recReq) {
		MessageTest.recReq = recReq;
	}

	public static MessageInterface getSqsx() {
		return sqsx;
	}

	public static void setSqsx(MessageInterface sqsx) {
		MessageTest.sqsx = sqsx;
	}

	public boolean isSendDoc() {
		return sendDoc;
	}

	public void setSendDoc(boolean sendDoc) {
		this.sendDoc = sendDoc;
	}

}
