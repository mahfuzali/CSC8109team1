/**
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

/**
 * @author Stephen Shephard
 *
 */
public class MessageApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean success = false;
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
	
		// Create a message queue name
	    String queueName = "QueueName" + UUID.randomUUID().toString();
	    
	    // Create a queue
	    success = sqsx.create(queueName);
        System.out.println("Created queue " + queueName + " " + success);
        
        // Send a registration request
        success = sqsx.registerRequest(queueName, "Alice", "PUBLICKEY");
        System.out.println("Sent registration request to queue " + queueName + " " + success);
        
        // Receive registration request
        String messageHandle = null;
        Message message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Userid:" + attributes.get("Userid").getStringValue());
        }
        
        // Delete registration request
        success = sqsx.deleteMessage(queueName, messageHandle);
        System.out.println("Deleted registration request from queue " + queueName + " " + success);
        
        // Send a message
        success = sqsx.sendMessage(queueName, "Exchange #1", "Simple test message #1", "Alice", "Bob");
        System.out.println("Sent message to queue " + queueName + " " + success);
        
        // Receive message
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }
        
        // Delete message
        success = sqsx.deleteMessage(queueName, messageHandle);
        System.out.println("Deleted message from queue " + queueName + " " + success);
        
        // Send a message with attached document
        success = sqsx.sendMsgDocument(queueName, "Exchange #1", "Simple test message #2", "src/main/resources/sample", "Alice", "Bob");
        System.out.println("Sent message and document to queue " + queueName + " " + success);
        
        // Receive message with attached document
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        ByteBuffer document;
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            System.out.println("  DocumentName:" + attributes.get("DocumentName").getStringValue());
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
        }
        
        // Delete message
        success = sqsx.deleteMessage(queueName, messageHandle);
        System.out.println("Deleted message from queue " + queueName + " " + success);
        
        // Send a message
        success = sqsx.sendMessage(queueName, "Exchange #1", "Simple test message #3", "Alice", "Bob");
        System.out.println("Sent message for Bob to queue " + queueName + " " + success);
        
        // Try to receive message for Alice
        messageHandle = null;
        message = sqsx.receiveMyMessage(queueName, "Alice");
        System.out.println("There is " + (message==null ? "no" : "a") + " message for Alice");
        
        // Try to receive message for Bob
        messageHandle = null;
        message = sqsx.receiveMyMessage(queueName, "Bob");
        System.out.println("There is " + (message==null ? "no" : "a") + " message for Bob");
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }
        
        // Send a registration request
        success = sqsx.registerRequest(queueName, "Alice", "Alice's public key");
        System.out.println("Sent registration request for Alice to queue " + queueName + " " + success);
        
        // Receive it then delete it
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Userid:" + attributes.get("Userid").getStringValue());
            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
        
        // Send a registration response
        success = sqsx.registerResponse(queueName, "Alice", "Alice's queue");

        // Receive it then delete it
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Target (TDS's POV):" + attributes.get("Target").getStringValue());
            System.out.println("  Queue:" + attributes.get("Queue").getStringValue());
            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
        
        // Send an exchange request
        success = sqsx.exchangeRequest(queueName, "CoffeySaidha", "SigA('ExchangeRequest')", "Alice", "Bob");
        
        // Receive it then delete it
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Protocol:" + attributes.get("Protocol").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
        
        // Send an exchange response
        success = sqsx.exchangeResponse(queueName, "Exchange #2", "ExchangeResponse", "Alice", "Bob", "Bob's public key");

        // Receive it then delete it
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            System.out.println("  TargetKey:" + attributes.get("TargetKey").getStringValue());
            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
        
        // Send an abort request
        success = sqsx.abortRequest(queueName, "Exchange #2", "SigA('AbortRequest')", "Alice", "Bob");

        // Receive it then delete it
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            System.out.println("  Abort:" + attributes.get("Abort").getStringValue());
            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
        
        // Send a message with Alice's public key
        success = sqsx.sendMsgSourceKey(queueName, "Exchange #3", "Test message", "Alice", "Bob", "ALICEKEY");

        // Receive it then delete it
        messageHandle = null;
        message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            System.out.println("  SourceKey:" + attributes.get("SourceKey").getStringValue());
            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        } 
      
        
        // Delete queue
        success = sqsx.delete(queueName);
        System.out.println("Deleted queue " + queueName + " " + success);
	}

}
