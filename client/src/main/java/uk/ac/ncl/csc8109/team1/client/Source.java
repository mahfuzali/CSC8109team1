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
import java.io.IOException;
import java.util.Map;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;


public class Source {
	private static final String Alice_QueueName = "csc8109_1_tds_queue_20070306_alice";
	private static final String TDS_QueueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";

	private static final String NAME = "Alice";
	private static String EOO;
	
	private static String queue; 
		
	public static void main(String[] args) throws IOException {
		String bobUUID = "7c715006-cedf-4921-a2ca-75e19e785d1c";

		Client alice = new Client(NAME);
		System.out.println(NAME + "'s Information");
		System.out.println("UUID: " + alice.getUUID().trim());
		System.out.println("Public Key: " + alice.getPublicKey());
		System.out.println("Private Key: " + alice.getPrivateKey());
		
	// Step 1: 
		//alice.regRequestForQueue(alice, TDS_QueueName_Reg);
		
	// Step 2: 
		//alice.getQueueNameFromTDS(TDS_QueueName_Reg, alice.getUUID());
		//alice.storeQueue(NAME, alice.getQueueName());
		
		/*
		//alice.replaceSelected(NAME, "Queue", alice.readQueueNameFromFile(NAME).trim());
		//alice.replaceSelected(NAME, "Target", bobUUID);
		System.out.println(NAME + "'s Queue Name: " + alice.readline(NAME, "Queue").trim());
		System.out.println(NAME + "'s Target's UUID: " + alice.readline(NAME, "Target").trim());
		*/
		
		
	// Step 3: Send TDS a exchange request
		/*
		String sigMsg =  alice.sigMsg("ExchangeRequest");
		System.out.println("Exchange Message Signture: " + sigMsg);
		sendExchangeRequest(TDS_QueueName, "CoffeySaidha", sigMsg, alice.getUUID(), bobUUID);
		*/
		
	// Step 4:
		//receiveExchangeResponse(alice, alice.readline(NAME, "Queue"));

		/*
		alice.setQueueName(alice.readline(NAME, "Queue").trim());
		alice.setLabel(alice.readline(NAME, "Label").trim());
		alice.setDestination(alice.readline(NAME, "Target").trim());
		System.out.println("Exchange Label: " + alice.readline(NAME, "Label").trim());
		System.out.println("Exchange Target: " + alice.readline(NAME, "Target").trim());
		
		String bobPublicKey = alice.readline(NAME, "RecipientPublicKey").trim();
		System.out.println(bobPublicKey);
		
		
		String shared = alice.sharedSecret(bobPublicKey);
		System.out.println(shared);

	
		alice.encrypt("resource/" + NAME + "/classified", "resource/" + NAME + "/enclassified", shared);
		File f = new File("resource/" + NAME + "/enclassified");
		*/
		
		
	// Step 5: encrypt file 	
		//System.out.println(alice.readline(NAME, "Queue"));
		//System.out.println("Message send status: " + sendDocMsg(f, alice, alice.readline(NAME, "Queue")));

	
    // Step 6:
		//receiveEORMsg(alice, alice.readline(NAME, "Queue"));
		
		
		
		
		
		
		
		
        
		/*
		alice.setLabel("label1");
		alice.setDestination("bob");

		alice.regRequest(alice, TDS_QueueName_Reg);
		
		// Change the Alice to uuid 
		alice.getQueueNameFromTDS(TDS_QueueName_Reg, alice.getUUID());
		System.out.println(alice.getQueueName());
		
		// Exchange record between multiple clients 
		String data = alice.getQueueName() + "," + alice.getLabel() + "," + alice.getDestination(); 
		alice.writeToFile("resource/"+ alice.getUUID() + "-exchange", data);
		*/
        
		
		/* Get bobs public key from the DB to compute shared key */
		/* Encrypt the file use the shared key */ 
		
		
		
		//File f = new File("sample");
		//System.out.println(sendDocMsg(f, alice, TDS_queueName));
		//receiveMsg(TDS_queueName);
		//receiveMsg(alice.getQueueName());
	}
	
	/**
	 * 
	 * @param f
	 * @param source
	 * @param tds_queue
	 * @return
	 */
	public static boolean sendDocMsg(File f, Client source, String tds_queue) {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		String queue = tds_queue;
		String label = source.getLabel();
		String eoo = source.getEOO(f);
		setEOO(eoo);
		String uuid = source.getUUID();
		String target = source.getDestination();

		
		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (eoo != null && !eoo.isEmpty())
				&& (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			//success = msg.sendMessage(source.getQueueName(), source.getLabel(), eoo, source.getUUID(), source.getDestination());
			
			System.out.println("EOO before sending");
			System.out.println(eoo);
			success = sqsx.sendMsgDocument(queue, label, eoo, f.getPath(), uuid, target);
			System.out.println(eoo);
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
	 * @throws IOException 
	 */
	public static void receiveEORMsg(Client c, String tds_queue) throws IOException {
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
            
            c.replaceSelected(NAME, "EOR", message.getBody());
            
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }		
	}

	public static void sendExchangeRequest(String queueName, String protocol, String sigMsg, String source, String target) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        
        // Send an exchange request
        success = sqsx.exchangeRequest(queueName, protocol, sigMsg, source, target);
	}
	
	public static void receiveExchangeResponse(Client c, String myQueue) throws IOException {
		boolean success = false;

		// Receive it then delete it
        String messageHandle = null;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        Message message = sqsx.receiveMessage(myQueue);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + myQueue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            c.setLabel(attributes.get("Label").getStringValue());
            
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            c.setDestination(attributes.get("Target").getStringValue());
            
            System.out.println("  TargetKey:" + attributes.get("TargetKey").getStringValue());
            c.setTargetPubKey(attributes.get("TargetKey").getStringValue());
            
            
    		c.replaceSelected(NAME, "Label", c.getLabel());
    		c.replaceSelected(NAME, "Target", c.getDestination());
    		c.replaceSelected(NAME, "RecipientPublicKey", c.getTargetPubKey());
            
            
            //success = sqsx.deleteMessage(myQueue, messageHandle);
            //System.out.println("Deleted message from queue " + myQueue + " " + success);
        }
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEOO() {
		return EOO;
	}

	/**
	 * 
	 * @param eOO
	 */
	static void setEOO(String eOO) {
		EOO = eOO;
	}

	/**
	 * 
	 * @return
	 */
	public static String getQueue() {
		return queue;
	}

	/**
	 * 
	 * @param q
	 */
	public static void setQueue(String q) {
		queue = q;
	}
	
}
