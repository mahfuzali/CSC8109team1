package uk.ac.ncl.csc8109.team1.client;

import java.io.File;
import java.util.Map;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class TDSExample {
	private static final String TDS_QueueName = "csc8109_1_tds_queue_20070306";
	private static final String Alice_QueueName = "csc8109_1_tds_queue_20070306_alice";
	private static final String Bob_QueueName = "csc8109_1_tds_queue_20070306_bob";

	private static final String name = "tds";

	private static String EOO;
	private static String EOR;
	

	private static String label;
	private static String target;
	private static String source;
	
	public static void main(String[] args) {
		
		// 1. Store the eor 
		receiveMsg(Bob_QueueName);
		System.out.println(getEOR());
		
		
		receiveMsg(TDS_QueueName);
		System.out.println(getEOO());
		
		//System.out.println(sendMsg(Bob_QueueName));
		receiveMsg(TDS_QueueName);

		
	}
	

	public static void receiveMsg(String tds_queue) {
		String queue = tds_queue;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		// Receive message
        String messageHandle = null;
        Message message = sqsx.receiveMessage(queue);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
    		setEOO(message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            setLabel(attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            setTarget(attributes.get("Target").getStringValue());
        }		
	}

	
	public static boolean sendMsg(String queue) {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		
		// 2. send the document that you received from alice to bob 
		success = sqsx.sendMsgDocument(queue, "Exchange #1", "Simple test message #2", "src/main/resources/sample", "Alice", "Bob");
		System.out.println("Sent message and document to queue " + queue + " " + success);
		
	
		// 3. send the eor to alice
		success = sqsx.sendMessage(queue, getLabel(), getEOR(), name, getSource());
	    System.out.println("Sent message to Alice " + queue + " " + success);

		success = sqsx.sendMessage(queue, getLabel(), getEOO(), name, getTarget());	
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
	}
	
	public static String getEOO() {
		return EOO;
	}

	static void setEOO(String eOO) {
		EOO = eOO;
	}

	public static String getEOR() {
		return EOR;
	}

	static void setEOR(String eOR) {
		EOR = eOR;
	}


	public static String getLabel() {
		return label;
	}


	static void setLabel(String l) {
		label = l;
	}


	public static String getTarget() {
		return target;
	}


	static void setTarget(String t) {
		target = t;
	}


	public static String getSource() {
		return source;
	}


	public static void setSource(String source) {
		TDSExample.source = source;
	}
	
	
}
