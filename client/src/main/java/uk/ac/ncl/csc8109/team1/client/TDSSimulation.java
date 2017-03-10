/*
 * Copyright (c) Mahfuz Ali - Team 1 CSC8109. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class TDSSimulation {
	private static final String TDS_QueueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";
	private static final String Alice_QueueName = "csc8109_1_tds_queue_20070306_alice";
	private static final String Bob_QueueName = "csc8109_1_tds_queue_20070306_bob";

	private static final String name = "tds";

	private static String EOO;
	private static String EOR;
	
	private static String label;
	private static String target;
	private static String source;
	
	private static String userId;
	
	public static void main(String[] args) {
	
		//receiveMsg(TDS_QueueName);
		//System.out.println(getEOO());

		//sendMsg(Bob_QueueName);
		
		//receiveMsg(TDS_QueueName);
		
		//receiveDocMsg(TDS_QueueName);
		
		//File f = new File("received");

		//boolean flag = sendDocMsg(f, Bob_QueueName);
		//System.out.println(flag);
		
		//receiveEORMsg(TDS_QueueName);
		//System.out.println(getEOR());
		//sendEORMsg(Alice_QueueName);
		
		receiveRegMsg(TDS_QueueName_Reg);
		String userid = getUserId();
		System.out.println(userid);
		
		returnQueueName(TDS_QueueName_Reg, userid);
	}
	
	/**
	 * 
	 * @param queueName
	 */
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
	
	/**
	 * 
	 * @param f
	 * @param queue
	 * @return
	 */
	public static boolean sendDocMsg(File f, String queue) {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		String label = TDSSimulation.getLabel();
		String source = TDSSimulation.getSource();
		String target = TDSSimulation.getTarget();

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

	/**
	 * 
	 * @param tds_queue
	 */
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

	/**
	 * 
	 * @param tds_queue
	 */
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
	
	/**
	 * 
	 * @param queue
	 * @return
	 */
	public static boolean sendMsg(String queue) {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		success = sqsx.sendMessage(queue, getLabel(), getEOO(), name, getTarget());	
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
	}
	
	/**
	 * 
	 * @param queue
	 * @return
	 */
	public static boolean sendEORMsg(String queue) {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		success = sqsx.sendMessage(queue, getLabel(), getEOR(), name + "-" + getSource(), getTarget());	
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
	}
	
	/**
	 * 
	 * @param tds_queue
	 */
	public static void receiveRegMsg(String tds_queue) {
				
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
        
        // Receive registration request
        String messageHandle = null;
        Message message = sqsx.receiveMessage(tds_queue);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + tds_queue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Userid:" + attributes.get("Userid").getStringValue());
            setUserId(attributes.get("Userid").getStringValue());
        }
	}

	/**
	 * 
	 * @param tdsQueueName
	 * @param source
	 */
	public static void returnQueueName(String tdsQueueName, String source) {        
		boolean success = false;
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
	
		// Create a message queue name
	    String queueName = "QueueName" + UUID.randomUUID().toString();
	    
	    // Create a queue
	    success = sqsx.create(queueName);
        System.out.println("Created queue " + queueName + " " + success);
        
        success = sqsx.registerResponse(tdsQueueName, source, queueName);
        System.out.println("Sent registration response (name, success): " + queueName + " " + success);

	}
	
	/**
	 * 
	 * @return
	 */
	public static String getEOO() {
		return EOO;
	}

	/**
	 * 
	 * @param eOO
	 */
	static void setEOO(String eOO) {
		EOO = eOO;
	}

	public static String getEOR() {
		return EOR;
	}

	/**
	 * 
	 * @param eOR
	 */
	static void setEOR(String eOR) {
		EOR = eOR;
	}

	/**
	 * 
	 * @return
	 */
	public static String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param l
	 */
	static void setLabel(String l) {
		label = l;
	}

	/**
	 * 
	 * @return
	 */
	public static String getTarget() {
		return target;
	}

	/**
	 * 
	 * @param t
	 */
	static void setTarget(String t) {
		target = t;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getSource() {
		return source;
	}

	/**
	 * 
	 * @param s
	 */
	static void setSource(String s) {
		source = s;
	}

	/**
	 * 
	 * @return
	 */
	public static String getUserId() {
		return userId;
	}

	/**
	 * 
	 * @param uid
	 */
	public static void setUserId(String uid) {
		userId = uid;
	}
	
}
