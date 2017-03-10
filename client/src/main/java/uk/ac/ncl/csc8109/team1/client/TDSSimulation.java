package uk.ac.ncl.csc8109.team1.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
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
	
		//receiveMsg(TDS_QueueName);
		//System.out.println(getEOO());

		//sendMsg(Bob_QueueName);
		
		//receiveMsg(TDS_QueueName);
		
		//receiveDocMsg(TDS_QueueName);
		
		//File f = new File("received");

		//boolean flag = sendDocMsg(f, Bob_QueueName);
		//System.out.println(flag);
		
		receiveEORMsg(TDS_QueueName);
		System.out.println(getEOR());
		sendEORMsg(Alice_QueueName);
	}
	
	
	public static void receiveDocMsg(String queueName) {
        // Receive message with attached document
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		String messageHandle = null;
        Message message = sqsx.receiveMessage(queueName);
        ByteBuffer document;
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            setLabel(attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            setSource(attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            setTarget(attributes.get("Target").getStringValue());
            System.out.println("  DocumentName:" + attributes.get("DocumentName").getStringValue());
            document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
            document.flip();
            
            OutputStream outputFile;
            WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("received");
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
	}
	
	
	public static boolean sendDocMsg(File f, String queue) {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		String label = TDSExample.getLabel();
		String source = TDSExample.getSource();
		String target = TDSExample.getTarget();

		System.out.println(label);
		System.out.println(source);
		System.out.println(target);

		
		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (source != null && !source.isEmpty())
				&& (target != null && !target.isEmpty())) {
				 
			success = sqsx.sendMsgDocument(queue, label, "Document from " + source, f.getPath(), name, target);
			System.out.println("EOO after sending");
	
			if (!success) {
				throw new IllegalArgumentException("null or empty value is passed");
			}
	
		}
		
		return success;
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

	
	public static void receiveEORMsg(String tds_queue) {
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
    		setEOR(message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            setLabel(attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            setSource(attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            setTarget(attributes.get("Target").getStringValue());
        }		
	}
	
	
	public static boolean sendMsg(String queue) {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		success = sqsx.sendMessage(queue, getLabel(), getEOO(), name, getTarget());	
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
	}
	
	
	public static boolean sendEORMsg(String queue) {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		success = sqsx.sendMessage(queue, getLabel(), getEOR(), name + "-" + getSource(), getTarget());	
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


	static void setSource(String s) {
		source = s;
	}
	
	
}
