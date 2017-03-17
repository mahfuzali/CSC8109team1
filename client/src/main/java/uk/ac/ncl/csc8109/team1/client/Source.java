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
import java.util.Scanner;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;

/**
 * This class represents a source client i.e. the sender
 * 
 * @author Mahfuz Ali
 * @Version 1.5
 * @email m.ali4@newcastle.ac.uk
 */
public class Source {

	private static final String TDS_QUEUE = "csc8109_1_tds_queue_20070306";
	private static final String TDS_REGISTRATION_QUEUE = "csc8109_1_tds_queue_20070306_reg";
	private static final String PROTOCOL_NAME = "CoffeySaidha";

	public static void main(String[] args) throws IOException, InterruptedException {
		/* Needs changing */
		String bobUUID = "2a497f70-7921-4115-b2a6-c1fa645bcacd";

		Client alice = new Client();
		System.out.println("Alice's Information");
		System.out.println("UUID: " + alice.getUUID());
		System.out.println("Public Key: " + alice.getPublicKey());
		System.out.println("Private Key: " + alice.getPrivateKey());

		alice.replaceSelected("Target", bobUUID);

		String[] items = { "Register and request for queue name", "Request for an exchange", "Send a document with EOO",
				"Get EOR from TDS", "Return label to TDS", "Send abort message", "End program" };

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
				System.out.print("You've chosen option #1: ");
				System.out.println("Register and request for queue name");
				
				// Step 1: Register with TDS and request for a queue name
				alice.regRequestForQueue(alice, TDS_REGISTRATION_QUEUE);
				while (alice.getQueueName() == null) {
					System.out.println("Waiting for the queue name...");
					Thread.sleep(5000);
					
					// Step 2: Get a queue name from the TDS
					alice.getQueueNameFromTDS(TDS_REGISTRATION_QUEUE, alice.getUUID());
				}
				alice.replaceSelected("Queue", alice.getQueueName());

				break;
			case 2:
				System.out.print("You've chosen option #2: ");
				System.out.println("Request for an exchange");
				
				// Step 3: Send TDS a exchange request
				String sigMsg = alice.sigMsg("ExchangeRequest");
				System.out.println("Exchange Message Signature: " + sigMsg);
				
				sendExchangeRequest(TDS_QUEUE, PROTOCOL_NAME, sigMsg, alice.getUUID(), bobUUID);
				while (alice.getLabel() == null && alice.getTargetPubKey() == null && alice.getDestination() == null) {
					System.out.println("Waiting for the exchange response...");
					
					Thread.sleep(5000);
					// Step 4: Receive response with a label and receiver's
					receiveExchangeResponse(alice, alice.readline("Queue").trim());
				}
				
				break;
			case 3:
				System.out.print("You've chosen option #3: ");
				System.out.println("Send a document with EOO");
				
				// Step 5: encrypt a file
				alice.setQueueName(alice.readline("Queue").trim());
				alice.setLabel(alice.readline("Label").trim());
				alice.setDestination(alice.readline("Target").trim());
				
				System.out.println("Exchange Label: " + alice.readline("Label").trim());
				System.out.println("Exchange Target: " + alice.readline("Target").trim());

				String bobPublicKey = alice.readline("RecipientPublicKey").trim();

				// Compute shared secret using the bob's public key
				String shared = alice.sharedSecret(bobPublicKey);
				System.out.println("Computed shared secret: " + shared);

				// encrypt the file using the shared secret
				alice.encrypt("classified", "enclassified", shared);
				File f = new File("enclassified");

				// Step 6: Send the encrypted file along with the EOO to TDS
				System.out.println("Message send status: " + sendDocMsg(f, alice, TDS_QUEUE));

				break;
			case 4:
				System.out.print("You've chosen option #4: ");
				System.out.println("Get EOR from TDS");
				
				while (alice.getEOR() == null) {
					System.out.println("Waiting for the EOR...");

					Thread.sleep(5000);
					// Step 7: Receive the eor from TDS
					receiveEORMsg(alice, alice.readline("Queue").trim());
				}
				break;
			case 5:
				System.out.print("You've chosen option #5: ");
				System.out.println("Return label to TDS");
				
				// Step 8: Return label
				alice.returnLabelToTds(TDS_QUEUE, alice.readline("Label").trim(), alice.getUUID(),
						alice.readline("Target").trim());
				break;
			case 6:
				System.out.print("You've chosen option #6: ");
				System.out.println("Send abort message");
				
				// Step 8: Send an abort message to TDS
				alice.abortRequest(TDS_QUEUE, alice.readline("Label").trim(), alice.getUUID(),
						alice.readline("Target").trim());

				while (alice.getAbort() == null) {
					System.out.println("Waiting for the abort response...");
					
					Thread.sleep(5000);
					alice.abortResponse(alice, alice.readline("Queue").trim());
				}

				System.out.println("About request accepted: " + alice.getAbort());

				break;
			case 0:
				quit = true;
				System.out.println("Exiting the program...");
				break;
			default:
				System.out.println("Invalid choice. Please, try again.");
			}
		} while (!quit);
		
		System.out.println("End of program");
		in.close();
	}

	/**
	 * Send the document to TDS, along with EOO 
	 * 
	 * @param <code>fileName</code> name of the file to send
	 * @param <code>source</code> sending client
	 * @param <code>tdsQueue</code> TDS queue
	 * @return <code>true</code>, if sent; otherwise, <code>false</code> 
	 */
	public static boolean sendDocMsg(File fileName, Client source, String tdsQueue) {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		String label = source.getLabel();
		String eoo = source.generateEOO(fileName);
		source.setEOO(eoo);
		String uuid = source.getUUID();
		String target = source.getDestination();

		if ((tdsQueue != null && !tdsQueue.isEmpty()) && (label != null && !label.isEmpty())
				&& (eoo != null && !eoo.isEmpty()) && (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			
			// Send TDS the document along with the EOO
			success = sqsx.sendMsgDocument(tdsQueue, label, eoo, fileName.getPath(), uuid, target);

			if (!success) {
				throw new IllegalArgumentException("null or empty value is passed");
			}
		}
		return success;
	}

	/**
	 * Receive the EOR from the TDS
	 * 
	 * @param <code>source</code> sending client
	 * @param <code>myQueue</code> source's queue
	 * @throws IOException
	 */
	public static void receiveEORMsg(Client source, String myQueue) throws IOException {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		// Receive message
		String messageHandle = null;
		Message message = sqsx.receiveMessage(myQueue);
		if (message != null) {
			messageHandle = message.getReceiptHandle();
			System.out.println("Message received from queue " + myQueue);
			System.out.println("  ID: " + message.getMessageId());
			System.out.println("  Receipt handle: " + messageHandle);
			System.out.println("  Message body: " + message.getBody());

			source.replaceSelected("EOR", message.getBody());
			source.setEOR(message.getBody());

			Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
			System.out.println("  Label:" + attributes.get("Label").getStringValue());
			System.out.println("  Source:" + attributes.get("Source").getStringValue());
			System.out.println("  Target:" + attributes.get("Target").getStringValue());

			System.out.println("EOR: " + source.getEOR());
			
			// Delete message
			success = sqsx.deleteMessage(myQueue, messageHandle);
			System.out.println("Deleted message from queue " + myQueue + " " + success);
		}
	}

	/**
	 * Send exchange request to TDS
	 * 
	 * @param <code>tdsQueue</code> TDS's queue 
	 * @param <code>protocol</code> protocol type be used in exchange 
	 * @param <code>sigMsg</code> signature of the request
	 * @param <code>source</code> sending client
	 * @param <code>target</code> receiving client
	 */
	public static boolean sendExchangeRequest(String tdsQueue, String protocol, String sigMsg, String source,
			String target) {
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		// Send an exchange request
		return sqsx.exchangeRequest(tdsQueue, protocol, sigMsg, source, target);
	}

	/**
	 * Receive exchange reponse back from TDS
	 * 
	 * @param <code>source</code> sending client
	 * @param <code>myQueue</code> source's queue
	 * @throws IOException
	 */
	public static void receiveExchangeResponse(Client source, String myQueue) throws IOException {
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
			source.setLabel(attributes.get("Label").getStringValue());

			System.out.println("  Source:" + attributes.get("Source").getStringValue());
			System.out.println("  Target:" + attributes.get("Target").getStringValue());
			source.setDestination(attributes.get("Target").getStringValue());

			System.out.println("  TargetKey:" + attributes.get("TargetKey").getStringValue());
			source.setTargetPubKey(attributes.get("TargetKey").getStringValue());

			source.replaceSelected("Label", source.getLabel());
			source.replaceSelected("Target", source.getDestination());
			source.replaceSelected("RecipientPublicKey", source.getTargetPubKey());

			System.out.println("Target's public key: " + source.getTargetPubKey());
			
			success = sqsx.deleteMessage(myQueue, messageHandle);
			System.out.println("Deleted message from queue " + myQueue + " " + success);
		}

	}

}
