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

public class Target {
	private static final String TDS_queueName = "csc8109_1_tds_queue_20070306";
	private static final String name = "bob-";
	
	private static String EOO;
	private static String EOR;

	public static void main(String[] args) {
		//**Starts exchange right away**//
		Client bob  = new Client();
		bob.setLabel("label1");
		bob.setSource("alice");
		bob.setQueueName("csc8109_1_tds_queue_20070306_bob");

		//**Wait for message2 from TDS **//
		//signature containing hash of doc (SigA (h(doc)))**//
		receiveMsg(bob);
		System.out.println(getEOO());
		
		/**/
		
		
		//**Send message3 to TDS
		//target signature containing signature of source(A) which contains hash of doc (sigB(sigA(h(doc)))**//
		sendMsg(bob, TDS_queueName, getEOO());	
			
		//**wait for doc from TDS**//
			
			
		//**Output then end**//
	}

	public static void receiveMsg(Client target) {
		String queue = target.getQueueName();
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
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }		
	}
	
	public static boolean sendMsg(Client source, String q, String eoo) {
		/*boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		success = sqsx.sendMessage(queue, source.getLabel(), getEOO(), name, source.getSource());	
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
		*/
		
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		

		String queue = q;
		String label = source.getLabel();
		String eor = source.getEOR(eoo);
		String uuid = source.getUUID();
		String target = source.getSource();

		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (eor != null && !eor.isEmpty())
				&& (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			
			success = sqsx.sendMessage(queue, source.getLabel(), eor, name + source.getUUID(), source.getSource());
			if (!success)
				throw new IllegalArgumentException("null or empty value is passed");
		}
	
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

}
