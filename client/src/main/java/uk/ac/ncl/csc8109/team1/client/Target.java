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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

/**
 * This class represents a target client i.e. the receiver
 * 
 * @author Mahfuz Ali
 * @Version 1.5
 * @email m.ali4@newcastle.ac.uk
 */
public class Target {
	private static final String TDS_QUEUE = "csc8109_1_tds_queue_20070306";
	private static final String TDS_REGISTRATION_QUEUE = "csc8109_1_tds_queue_20070306_reg";

	public static void main(String[] args) throws IOException, InterruptedException {
		String aliceUUID = "273a22a1-4002-44f8-9561-f6101d2fd074";

		Client bob = new Client();
		System.out.println("Bob's Information:");
		System.out.println("UUID: " + bob.getUUID().trim());
		System.out.println("Public Key: " + bob.getPublicKey());
		System.out.println("Private Key: " + bob.getPrivateKey());

		bob.replaceSelected("Target", aliceUUID);

		String[] items = { "Register and request for queue name", "Get EOO from TDS", "Send EOR message to TDS",
				"Check TDS for a document", "Return label to TDS", "Send abort message", "End program" };

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
			System.out.print("Choose a menu item: ");
			menuItem = in.nextInt();
			switch (menuItem) {
			case 1:
				System.out.print("You've chosen item #1: ");
				System.out.println("Register and request for queue name");

				// Step 1: Register with TDS and request for a queue name
				bob.regRequestForQueue(bob, TDS_REGISTRATION_QUEUE);
				while (bob.getQueueName() == null) {
					System.out.println("Waiting for the queue name...");
					
					Thread.sleep(5000);
					// Step 2: Get a queue name from the TDS
					bob.getQueueNameFromTDS(TDS_REGISTRATION_QUEUE, bob.getUUID());
				}

				bob.replaceSelected("Queue", bob.getQueueName());

				break;
			case 2:
				System.out.print("You've chosen item #2: ");
				System.out.println("Get EOO from TDS");
				
				while (bob.getEOO() == null) {
					System.out.println("Waiting for the EOO...");

					Thread.sleep(5000);
					// Step 3: Receive EOO from TDS
					receiveEOOMsg(bob);
				}

				break;
			case 3:
				System.out.print("You've chosen item #3: ");
				System.out.println("Send EOR message to TDS");
				
				String eoo = bob.readline("EOO");
				// Step 4: Send to EOR to TDS
				sendEORMsg(TDS_QUEUE, bob.readline("Label"), bob.generateEOR(eoo), bob.getUUID().trim(),
						bob.readline("Target"));

				break;
			case 4:
				System.out.print("You've chosen item #4: ");
				System.out.println("Check TDS for a document");
				
				while (bob.isReceivedDoc() == false) {
					System.out.println("Waiting to receive document...");
					
					Thread.sleep(5000);
					// Step 5: Receive the document from TDS
					receiveDocMsg(bob, bob.readline("Queue"));					
				}

				break;
			case 5:
				System.out.print("You've chosen item #5: ");
				System.out.println("Return label to TDS");
				
				// Step 6: Return label
				bob.returnLabelToTds(TDS_QUEUE, bob.readline("Label"), bob.getUUID(), bob.readline("Target"));

				break;
			case 6:
				System.out.print("You've chosen item #6: ");
				System.out.println("Send abort message");
				
				// Step 7: Send an abort message to TDS
				bob.abortRequest(TDS_QUEUE, bob.readline("Label").trim(), bob.getUUID(),
						bob.readline("Target").trim());

				while (bob.getAbort() == null) {
					System.out.println("Waiting for the abort response...");

					Thread.sleep(5000);
					// Step 8: Receive abort response
					bob.abortResponse(bob, bob.readline("Queue").trim());
				}

				System.out.println("Abort request accepted: " + bob.getAbort());

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
	 * Receives a document from the queue
	 * 
	 * @param <code>target</code> target client
	 * @param <code>myQueue</code> target's queue
	 * @throws IOException
	 */
	public static void receiveDocMsg(Client target, String myQueue) throws IOException {
		boolean success = false;
		// Receive message with attached document
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		String messageHandle = null;
		Message message = sqsx.receiveMessage(myQueue);
		ByteBuffer document;
		if (message != null) {
			messageHandle = message.getReceiptHandle();
			System.out.println("Message received from queue " + myQueue);
			System.out.println("  ID: " + message.getMessageId());
			System.out.println("  Receipt handle: " + messageHandle);
			System.out.println("  Message body: " + message.getBody());
			// setEOO(message.getBody());
			Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
			System.out.println("  Label:" + attributes.get("Label").getStringValue());
			System.out.println("  Source:" + attributes.get("Source").getStringValue());
			System.out.println("  Target:" + attributes.get("Target").getStringValue());
			System.out.println("  DocumentName:" + attributes.get("DocumentName").getStringValue());
			document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
			document.flip();

			target.replaceSelected("Label", attributes.get("Label").getStringValue());
			target.replaceSelected("Target", attributes.get("Target").getStringValue());

			OutputStream outputFile;
			WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("recClassified");
				outputChannel = Channels.newChannel(outputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				outputChannel.write(document);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String shared = target.sharedSecret(target.readline("RecipientPublicKey"));
			target.decrypt("recClassified", "deClassified", shared);

			target.setReceivedDoc(true);
			
			// Delete message
			success = sqsx.deleteMessage(myQueue, messageHandle);
			System.out.println("Deleted message from queue " + myQueue + " " + success);

		}

	}

	/**
	 * Receives EOO from TDS
	 * @param <code>target</code> target client
	 * @throws IOException
	 */
	public static void receiveEOOMsg(Client target) throws IOException {
		boolean success = false;
		String queue = target.readline("Queue");
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
			target.setEOO(message.getBody());
			Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
			System.out.println("  Label:" + attributes.get("Label").getStringValue());

			System.out.println("  Source:" + attributes.get("Source").getStringValue());
			System.out.println("  Target:" + attributes.get("Target").getStringValue());
			System.out.println("  SourceKey:" + attributes.get("SourceKey").getStringValue());

			target.setLabel(attributes.get("Label").getStringValue().trim());
			target.setDestination(attributes.get("Source").getStringValue().trim());
			target.setSourcePubKey(attributes.get("SourceKey").getStringValue().trim());

			target.replaceSelected("Label", target.getLabel());
			target.replaceSelected("Target", target.getDestination());
			target.replaceSelected("EOO", target.getEOO());
			target.replaceSelected("RecipientPublicKey", target.getSourcePubKey());

			System.out.println("Received EOO from TDS: " + target.getEOO());
			
			// Delete message
			success = sqsx.deleteMessage(target.readline("Queue"), messageHandle);
			System.out.println("Deleted message from queue " + target.readline("Queue") + " " + success);

		}

	}

	/**
	 * Send the EOR message to TDS
	 * 
	 * @param <code>tdsQueue</code> TDS's queue
	 * @param </code>label</code> exchange label
	 * @param </code>eor</code> eor to be send to TDS
	 * @param <code>uuid</code> source's uuid
	 * @param <code>target</code> target's uuid
	 * @return
	 * @throws IOException
	 */
	public static boolean sendEORMsg(String tdsQueue, String label, String eor, String uuid, String target)
			throws IOException {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		if ((tdsQueue != null && !tdsQueue.isEmpty()) && (label != null && !label.isEmpty())
				&& (eor != null && !eor.isEmpty()) && (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {

			success = sqsx.sendMessage(tdsQueue, label, eor, uuid, target);
			if (!success)
				throw new IllegalArgumentException("null or empty value is passed");
		}

		return success;
	}
	
}
