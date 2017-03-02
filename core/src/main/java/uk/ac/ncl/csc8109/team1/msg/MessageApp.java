/**
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import java.util.Arrays;
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
		MessageInterface sqsx = new AmazonExtendedSQS();
		System.out.println("Initialised queue service");

	    // Create a long string of characters for the message object to be stored in the bucket
	    int stringLength = 300000;
	    char[] chars = new char[stringLength];
	    Arrays.fill(chars, 'x');
	    String myLongString = new String(chars);
		
		// Create a message queue name
	    String queueName = "QueueName" + UUID.randomUUID().toString();
	    
	    // Create a queue
	    success = sqsx.create(queueName);
        System.out.println("Created queue " + queueName + " " + success);
        
        // Send a message
        success = sqsx.sendMessage(queueName, myLongString, "Bob");
        System.out.println("Sent message to queue " + queueName + " " + success);
        
        // Receive message
        String messageHandle = null;
        Message message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body (first 5 characters): " + message.getBody().substring(0, 5));
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }
        
        // Delete message
        // success = sqsx.deleteMessage(queueName, messageHandle);
        // System.out.println("Deleted message from queue " + queueName + " " + success);
        
        // Delete queue
        // success = sqsx.delete(queueName);
        // System.out.println("Deleted queue " + queueName + " " + success);
	}

}
