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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;

/** 
 * This class represents a source client i.e. the sender
 * 
 * @author Mahfuz Ali
 * @Version 1.3
 * @email m.ali4@newcastle.ac.uk
 */
public class Source {
	//private static final String Alice_QueueName = "csc8109_1_tds_queue_20070306_alice";
	private static final String TDS_QueueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";
	private static final String PROTOCOL_NAME = "CoffeySaidha";
	
	//private static final String NAME = "Alice";


	
	public static void main(String[] args) throws IOException, InterruptedException {
		/* Needs changing */
		String bobUUID = "5ddf9afd-2796-4e44-b20b-d642462ba1d1";


		Client alice = new Client();
		System.out.println("Alice's Information");
		System.out.println("UUID: " + alice.getUUID());
		System.out.println("Public Key: " + alice.getPublicKey());
		System.out.println("Private Key: " + alice.getPrivateKey());

		alice.replaceSelected("Target", bobUUID);

		
		String[] items = { "Register and request for queue name", 
							"Request for an exchange", 
							"Send a document with EOO",
							"Get EOR from TDS", 
							"Return label to TDS", 
							"Send abort message", 
							"End program" };

		Scanner in = new Scanner(System.in);
		// Print menu
		for (int i = 1; i <= 7; i++) {
			System.out.println(i + ". " + items[i - 1]);
		}
		System.out.println("0. Quit");

		// Handle user commands
		boolean quit = false;
		int menuItem;

		do {
			System.out.print("Choose a menu option: ");
			menuItem = in.nextInt();
			
			switch (menuItem) {
			case 1:
				System.out.println("You've chosen option #1");
				// Step 1: Register with TDS and request for a queue name
				
				alice.regRequestForQueue(alice, TDS_QueueName_Reg);
				while (alice.getQueueName() == null) {
					Thread.sleep(5000);
					// Step 2: Get a queue name from the TDS
					alice.getQueueNameFromTDS(TDS_QueueName_Reg,
							alice.getUUID());
				}
				alice.replaceSelected("Queue", alice.getQueueName());

				break;
			case 2:
				System.out.println("You've chosen option #2");
				// Step 3: Send TDS a exchange request
				String sigMsg = alice.sigMsg("ExchangeRequest");
				System.out.println("Exchange Message Signature: " + sigMsg);
				sendExchangeRequest(TDS_QueueName, PROTOCOL_NAME, sigMsg, alice.getUUID(), bobUUID);
				while (alice.getLabel() == null && alice.getTargetPubKey() == null) {
					Thread.sleep(5000);
					// Step 4: Receive response with a label and receiver's public key 
					receiveExchangeResponse(alice, alice.readline("Queue").trim());
				}
				break;
			case 3:
				System.out.println("You've chosen option #3");

				// Step 5: encrypt a file
				alice.setQueueName(alice.readline("Queue").trim());
				alice.setLabel(alice.readline("Label").trim());
				alice.setDestination(alice.readline("Target").trim());
				System.out.println("Exchange Label: " + alice.readline("Label").trim());
				System.out.println("Exchange Target: " + alice.readline("Target").trim());

				String bobPublicKey = alice.readline("RecipientPublicKey").trim();
				System.out.println(bobPublicKey);

				// Compute shared secret
				String shared = alice.sharedSecret(bobPublicKey);
				System.out.println(shared);

				alice.encrypt("classified", "enclassified", shared);
				File f = new File("enclassified");

				// Step 6: Send the encrypted file to TDS
				//System.out.println(alice.readline(NAME, "Queue").trim());
				System.out.println("Message send status: " + sendDocMsg(f, alice, TDS_QueueName));

				break;
			case 4:
				System.out.println("You've chosen option #4");
				// Step 7: Receive the eor from TDS
				while (alice.getEOR() == null) {
					Thread.sleep(5000);
					receiveEORMsg(alice, alice.readline("Queue").trim());
				}
				break;
			case 5:
				System.out.println("You've chosen option #5");
				// Step 8: Return label 
				alice.returnLabelToTds(TDS_QueueName, alice.readline("Label").trim(), alice.getUUID(), alice.readline("Target").trim());
				break;
			case 6:
				System.out.println("You've chosen option #6");
				// Step 8: Send an abort message to TDS
				alice.abortRequest(TDS_QueueName, alice.readline("Label").trim(), alice.getUUID(), alice.readline("Target").trim()); 

				while (alice.getAbort() != null) {
					Thread.sleep(5000);
					alice.abortResponse(alice, alice.readline("Queue").trim());
				}
				
				System.out.println("About request accepted: " + alice.getAbort());
				
				break;
			case 0:
				quit = true;
				break;
			default:
				System.out.println("Invalid choice. Please, try again.");
			}
		} while (!quit);
		System.out.println("End of program");
		
	}
	
	public static void exchange() {
	// Step 1: 
		//alice.regRequestForQueue(alice, TDS_QueueName_Reg);
		
	// Step 2: 
		//alice.getQueueNameFromTDS(TDS_QueueName_Reg, new String(Files.readAllBytes(Paths.get("resource/Alice/UUID"))).trim() );
		//alice.replaceSelected(NAME, "Queue", alice.getQueueName());
		//alice.replaceSelected(NAME, "Target", bobUUID);
		
		/*
	// Step 3: Send TDS a exchange request
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
		String eoo = source.generateEOO(f);
		source.setEOO(eoo);
		String uuid = source.getUUID();
		String target = source.getDestination();
		
		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (eoo != null && !eoo.isEmpty())
				&& (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			
			success = sqsx.sendMsgDocument(queue, label, eoo, f.getPath(), uuid, target);

			if (!success) {
				throw new IllegalArgumentException("null or empty value is passed");
			}
		}
		return success;
	}
	
	/**
	 * 
	 * @param c
	 * @param tds_queue
	 * @throws IOException
	 */
	public static void receiveEORMsg(Client c, String tds_queue) throws IOException {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		// Receive message
        String messageHandle = null;
        Message message = sqsx.receiveMessage(tds_queue);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + tds_queue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            
            c.replaceSelected("EOR", message.getBody());
            c.setEOR(message.getBody());
            
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            
            
            // Delete message
            success = sqsx.deleteMessage(tds_queue, messageHandle);
            System.out.println("Deleted message from queue " + tds_queue + " " + success);
            
        }		
        

	}

	/**
	 * 
	 * @param queueName
	 * @param protocol
	 * @param sigMsg
	 * @param source
	 * @param target
	 */
	public static void sendExchangeRequest(String queueName, String protocol, String sigMsg, String source, String target) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        // Send an exchange request
        success = sqsx.exchangeRequest(queueName, protocol, sigMsg, source, target);
	}
	
	/**
	 * 
	 * @param c
	 * @param myQueue
	 * @throws IOException
	 */
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

    		c.replaceSelected("Label", c.getLabel());
    		c.replaceSelected("Target", c.getDestination());
    		c.replaceSelected("RecipientPublicKey", c.getTargetPubKey());
              
            success = sqsx.deleteMessage(myQueue, messageHandle);
            System.out.println("Deleted message from queue " + myQueue + " " + success);
        }
      
	}

}
