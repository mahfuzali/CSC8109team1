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
 * @Version 1.3
 * @email m.ali4@newcastle.ac.uk
 */
public class Target {
	private static final String TDS_QueueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";

	private static final String NAME = "Bob";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String aliceUUID = "2ed5457b-9c94-4979-baec-9e9e786d8508";
		
		Client bob = new Client();
		System.out.println("Bob's Information:");
		System.out.println("UUID: " + bob.getUUID().trim());
		System.out.println("Public Key: " + bob.getPublicKey());
		System.out.println("Private Key: " + bob.getPrivateKey());
		
		
		String[] items = {"Register and request for queue name",
				  "Get EOO from TDS",
				  "Send EOR message to TDS",
				  "Check TDS for a document",
				  "Return label to TDS", 
				  "Send abort message",
				  "End program"};
		
		Scanner in = new Scanner(System.in);
		// print menu
		for (int i = 1; i <= 7; i++) {
		  System.out.println(i + ". " + items[i-1]);        	
		}
		System.out.println("0. Quit");
		
		// handle user commands
		boolean quit = false;
		int menuItem;
		
		do {
		    System.out.print("Choose menu item: ");
		    menuItem = in.nextInt();
		    switch (menuItem) {
		    case 1:
		          System.out.println("You've chosen item #1");
		          // do something...
				  // Step 1: 
		  		  bob.regRequestForQueue(bob, TDS_QueueName_Reg);
		  		  while(bob.getQueueName() == null) {

					 Thread.sleep(10000);
				   // Step 2: 
					 bob.getQueueNameFromTDS(TDS_QueueName_Reg, bob.getUUID() );
					 //bob.replaceSelected(NAME, "Target", aliceUUID);
				  }

		  		  bob.replaceSelected("Queue", bob.getQueueName());

		          break;
		    case 2:
		          System.out.println("You've chosen item #2");
		          // do something...
		          
		          while(bob.getEOO() == null){
			      	  // Step 3: 
					  Thread.sleep(10000);

			  		  receiveEOOMsg(bob);  
		          }
		
		          break;
		    case 3:
		          System.out.println("You've chosen item #3");
		          // do something...
		          
		          String eoo = bob.readline("EOO");
		  		  sendEORMsg(bob.readline("Queue"), bob.readline("Label"), bob.generateEOR(eoo), bob.getUUID().trim(), bob.readline("Target"));
		  		
		          break;
		    case 4:
		          System.out.println("You've chosen item #4");
		          // do something...
		          // Step 5: 
				  Thread.sleep(5000);

		      	  receiveDocMsg(bob, bob.readline("Queue"));
		          
		          break;
		    case 5:
		          System.out.println("You've chosen item #5");
		          // do something...		          
		          bob.returnLabelToTds(bob.readline("Queue"), bob.readline("Label"), bob.getUUID(), bob.readline("Target"));

		          break;
		    case 6:
		          System.out.println("You've chosen item #6");
		          // do something...
		          break;
		    case 0:
		          quit = true;
		          break;
		    default:
		          System.out.println("Invalid choice.");
		    }
		} while (!quit);
		System.out.println("Bye-bye!");

	}

	
	public static void exchange() {
	 // Step 1: 
		//bob.regRequestForQueue(bob, TDS_QueueName_Reg);
	
	 // Step 2: 
		//bob.getQueueNameFromTDS(TDS_QueueName_Reg, new String(Files.readAllBytes(Paths.get("resource/Bob/UUID"))).trim() );
		//bob.replaceSelected(NAME, "Queue", bob.getQueueName());
		//bob.replaceSelected(NAME, "Target", aliceUUID);

		
	// Step 3: 
		//receiveEOOMsg(bob);
		
    // Step 4: 
		//String eoo = bob.readline(NAME, "EOO");
		//sendEORMsg(bob.readline(NAME, "Queue"), bob.readline(NAME, "Label"), bob.getEOR(eoo), bob.getUUID().trim(), bob.readline(NAME, "Target"));
		
		
	// Step 5: 
		//receiveDocMsg(bob, bob.readline(NAME, "Queue"));
		
		/**/
	// Step 6: 
		//String sigMsg =  bob.sigMsg("PublicKeyRequest");
		//System.out.println("Exchange Message Signture: " + sigMsg);
		//sendPubKeyRequest(TDS_QueueName, "PublicKeyRequest", sigMsg, bob.getUUID(), aliceUUID);
		
		
	// Step 7:
		//receivePubKeyResponse(bob, bob.readline(NAME, "Queue"));

		//String shared = bob.sharedSecret(bob.readline(NAME, "RecipientPublicKey"));
		//bob.decrypt("resource/" + NAME + "/recClassified", "resource/" + NAME + "/deClassified", shared);
	}
	
	
	/**
	 * 
	 * @param myQueue
	 * @throws IOException 
	 */
	public static void receiveDocMsg(Client c, String myQueue) throws IOException {
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
            //setEOO(message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            System.out.println("  DocumentName:" + attributes.get("DocumentName").getStringValue());
            document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
            document.flip();
            
            
            c.replaceSelected("Label", attributes.get("Label").getStringValue());
            c.replaceSelected("Target", attributes.get("Target").getStringValue());
            
            
            OutputStream outputFile;
            WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("/recClassified");
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
            
            // Delete message
            success = sqsx.deleteMessage(myQueue, messageHandle);
            System.out.println("Deleted message from queue " + myQueue + " " + success);
            
            
        }
        

	}
	
	/**
	 * 
	 * @param target
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
            
            
            // Delete message
            success = sqsx.deleteMessage(target.readline("Queue"), messageHandle);
            System.out.println("Deleted message from queue " + target.readline("Queue") + " " + success);
            
        }		
       

	}
	
	/**
	 * 
	 * @param tdsQueue
	 * @param label
	 * @param eor
	 * @param uuid
	 * @param target
	 * @return
	 * @throws IOException
	 */
	public static boolean sendEORMsg(String tdsQueue, String label, String eor, String uuid, String target) throws IOException {
		boolean success = false;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		if ((tdsQueue != null && !tdsQueue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (eor != null && !eor.isEmpty())
				&& (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			
			success = sqsx.sendMessage(tdsQueue, label, eor, uuid, target);
			if (!success)
				throw new IllegalArgumentException("null or empty value is passed");
		}
	
		return success;
	}
	
	/**
	 * 
	 * @param tdsQueue
	 * @param protocol
	 * @param sigMsg
	 * @param source
	 * @param target
	 */
	public static void sendPubKeyRequest(String tdsQueue, String protocol, String sigMsg, String source, String target) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        
        // Send an exchange request
        success = sqsx.exchangeRequest(tdsQueue, protocol, sigMsg, source, target);
	}
	
	/**
	 * 
	 * @param c
	 * @param myQueue
	 * @throws IOException
	 */
	public static void receivePubKeyResponse(Client c, String myQueue) throws IOException {
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
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());

            System.out.println("  TargetKey:" + attributes.get("TargetKey").getStringValue());
            c.setTargetPubKey(attributes.get("TargetKey").getStringValue());
            
    		c.replaceSelected("RecipientPublicKey", c.getTargetPubKey());
            
            success = sqsx.deleteMessage(myQueue, messageHandle);
            System.out.println("Deleted message from queue " + myQueue + " " + success);
        }
	}
	

}
