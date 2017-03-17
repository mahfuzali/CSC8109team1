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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	private static String protocol;

	private static String alice_publicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEHvUwmV+Xd0RfY2sy30MZIKKqcmmaGhovMbnlH9amAu+CZyAzLfN1RdY09QSmTN+cWcOuxQBv6FjXCHnK4eSSOQ==";
	private static String bob_publicKey =   "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAExB36U3xFK0otkAre0+pls35E0CMLycooSFTHucCCNGdSnA/8CLbgjbtKVhMz/u1crzIKaqF7qo/HQ2DrEPeqBg==";
	
	
	public static void main(String[] args) throws IOException {
		String aliceExchangeQ = "QueueName-4fabc628-d20a-4cd4-904e-cd729315345e";
		String bobExchangeQ = "QueueName-0b359e5a-6f82-47e8-bc71-3b6cc97d424e";
		
	// Step 1: 
		//receiveQueueNameRequestMsg(TDS_QueueName_Reg);
		
		/**/	
	// Step 2: 
		//String userid = readline("Source");
		//String userid = readline("Target");
		//sendQueueNameToClient(TDS_QueueName_Reg, userid);
		
		
	// Step 3: 
		//receiveClientExchangeRequest(TDS_QueueName);
		//sendClientExchangeResponse(aliceExchangeQ, readline("Protocol"), readline("Source"), readline("Target"), bob_publicKey);
		
		
	//Step 4: 
		//receiveDocMsg(aliceExchangeQ);
		
		
	//Step 5: 
		//sendEOOMsg(bobExchangeQ, readline("Label"), readline("EOO"), readline("Source"), readline("Target"), alice_publicKey);

		
	// Step 6:
		//receiveEORMsg(bobExchangeQ);
		
	// Step 7: 
		//File f = new File("resource/TDS/received");
		//sendDocMsg(f, bobExchangeQ, readline("Label"), readline("Source"), readline("Target"));
		
	
	// Step 8:
		//sendEORMsg(aliceExchangeQ, readline("Label"), readline("EOR"), readline("Source"), readline("Target"));
		
		
	// Step 9: 	
		//receiveClientPubKeyRequest(TDS_QueueName);
		//sendClientPubKeyResponse(bobExchangeQ, readline("Protocol"), readline("Target"), readline("Source"), alice_publicKey);

		
		
		
	}
	
	/**
	 * 
	 * @param queueName
	 * @throws IOException 
	 */
	public static void receiveDocMsg(String queueName) throws IOException {
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
            setEOO(message.getBody());
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
            
            replaceSelected("EOO", getEOO().trim());
            replaceSelected("Label", getLabel().trim());
            replaceSelected("Source", getSource().trim());
            replaceSelected("Target", getTarget().trim());
            replaceSelected("DocumentName", attributes.get("DocumentName").getStringValue().trim());

            
            OutputStream outputFile;
            WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("resource/TDS/received");
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
	public static boolean sendDocMsg(File f, String queue, String label, String source, String target) {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		

		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (source != null && !source.isEmpty())
				&& (target != null && !target.isEmpty())) {
				 
			success = sqsx.sendMsgDocument(queue, label, "Document from " + source, f.getPath(), source, target);
			//System.out.println("EOO after sending");
	
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
	 * @throws IOException 
	 */
	public static void receiveEORMsg(String tds_queue) throws IOException {
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
    		
    		replaceSelected("EOR", getEOR());
    		
    		
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            setLabel(attributes.get("Label").getStringValue().trim());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            setSource(attributes.get("Source").getStringValue().trim());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            setTarget(attributes.get("Target").getStringValue().trim());
           
        }		
	}
	
	/**
	 * 
	 * @param queue
	 * @return
	 * @throws IOException 
	 */
	public static boolean sendEOOMsg(String queue, String label, String EOO, String source, String target, String sourcekey) throws IOException {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		//success = sqsx.sendMessage(queue, label, EOO, source, target);
		success = sqsx.sendMsgSourceKey(queue, label, EOO, source, target, sourcekey);
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
	}
	
	/**
	 * 
	 * @param queue
	 * @return
	 */
	public static boolean sendEORMsg(String queue, String label, String eor, String source, String target) {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		success = sqsx.sendMessage(queue, label, eor, source, target);	
		if (!success)
			throw new IllegalArgumentException("null or empty value is passed");

		return success;
	}
	
	/**
	 * 
	 * @param tdsRegistrstionQueue
	 * @throws IOException 
	 */
	public static void receiveQueueNameRequestMsg(String tdsRegistrstionQueue) throws IOException {
				
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
        
        // Receive registration request
        String messageHandle = null;
        Message message = sqsx.receiveMessage(tdsRegistrstionQueue);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + tdsRegistrstionQueue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Userid:" + attributes.get("Userid").getStringValue());
            setUserId(attributes.get("Userid").getStringValue());
            
            //replaceSelected("Source", attributes.get("Userid").getStringValue().trim());
            replaceSelected("Target", attributes.get("Userid").getStringValue().trim()); 
        }
        
        
        
	}

	/**
	 * 
	 * @param tdsQueueName
	 * @param source
	 * @throws IOException 
	 */
	public static void sendQueueNameToClient(String tdsQueueName, String source) throws IOException {        
		boolean success = false;
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
	
		// Create a message queue name
	    String queueName = "QueueName-" + UUID.randomUUID().toString();
	    
	    // Create a queue
	    success = sqsx.create(queueName);
        System.out.println("Created queue " + queueName + " " + success);
        
        //replaceSelected("RecQ", queueName);
        replaceSelected("SendQ", queueName);

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

	
	public static String getProtocol() {
		return protocol;
	}

	public static void setProtocol(String p) {
		protocol = p;
	}
	
	/**
	 * 
	 * @param tdsQueueName
	 * @throws IOException
	 */
	public static void receiveClientExchangeRequest(String tdsQueueName) throws IOException {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		// Receive it then delete it
        String messageHandle = null;
        Message message = sqsx.receiveMessage(tdsQueueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + tdsQueueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Protocol:" + attributes.get("Protocol").getStringValue());
            setProtocol(attributes.get("Protocol").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            setSource(attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            setTarget(attributes.get("Target").getStringValue());
            
            replaceSelected("Protocol", getProtocol().trim());
            replaceSelected("Source", attributes.get("Source").getStringValue().trim());
            replaceSelected("Target", getTarget().trim());

            
            //success = sqsx.deleteMessage(TDS_QueueName, messageHandle);
            //System.out.println("Deleted message from queue " + TDS_QueueName + " " + success);
        }
	}
	
	/**
	 * 
	 * @param senderPrivateQueue
	 * @param protocol
	 * @param source
	 * @param target
	 * @param targetPublicKey
	 */
	public static void sendClientExchangeResponse(String senderPrivateQueue, String protocol, String source, String target, String targetPublicKey) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		String label = UUID.randomUUID().toString();

        // Send an exchange response to source's queue
        success = sqsx.exchangeResponse(senderPrivateQueue, label, "ExchangeResponse", source, target, targetPublicKey);
	}
	
	
	/**
	 * Replaces specfic line from key file
	 * 
	 * @param startofline
	 * @param data
	 * @param iv
	 * @throws IOException
	 */
	public static void replaceSelected(String startofline, String data) throws IOException {
		String store = "";
		try {
			File file = new File("resource/TDS/exchangeRecord");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
				if (line.startsWith(startofline)) {
					store = line;
				}
			}
			reader.close();

			String[] tmp = store.split(" : ");

			String newtext = oldtext.replace(tmp[1], data);

			FileWriter writer = new FileWriter("resource/TDS/exchangeRecord");
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
	/**
	 * Read a specific line
	 * 
	 * @param startofline
	 * @return
	 * @throws IOException
	 */
	public static String readline(String startofline) throws IOException {
        String a = "";
        	
        File newfile = new File("resource/TDS/exchangeRecord");
        BufferedReader reader = new BufferedReader(new FileReader(newfile));
        String line = "";
        
        while((line = reader.readLine()) != null)
        {
            if(line.startsWith(startofline))
            	a = line;
        }
        
        reader.close();
        
        String[] tmp = a.split(" : ");

        return tmp[1];
	}
	
	/**
	 * 
	 * @param tdsQueueName
	 */
	public static void receiveClientPubKeyRequest(String tdsQueueName) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		// Receive it then delete it
        String messageHandle = null;
        Message message = sqsx.receiveMessage(tdsQueueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + tdsQueueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Protocol:" + attributes.get("Protocol").getStringValue());
            setProtocol(attributes.get("Protocol").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            setSource(attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            setTarget(attributes.get("Target").getStringValue());
            //success = sqsx.deleteMessage(TDS_QueueName, messageHandle);
            //System.out.println("Deleted message from queue " + TDS_QueueName + " " + success);
        }
	}
	
	/**
	 * 
	 * @param senderPrivateQueue
	 * @param label
	 * @param source
	 * @param target
	 * @param targetPublicKey
	 */
	public static void sendClientPubKeyResponse(String senderPrivateQueue, String label, String source, String target, String targetPublicKey) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
        // Send an exchange response to source's queue
        success = sqsx.exchangeResponse(senderPrivateQueue, label, "PublicKeyRequest", source, target, targetPublicKey);
	}

}
