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
import java.util.Map;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class Target {
	private static final String TDS_QueueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";

	private static final String NAME = "Bob";
	
	private static String EOO;
	private static String EOR;

	public static void main(String[] args) throws IOException {
		Client bob = new Client(NAME);
		System.out.println(NAME + "'s Information:");
		System.out.println("UUID: " + bob.getUUID().trim());
		System.out.println("Public Key: " + bob.getPublicKey());
		System.out.println("Private Key: " + bob.getPrivateKey());
		
		String aliceUUID = "aeefe21e-dbec-4dab-a1ae-ac19240675e4";

		
	 // Step 1: 
		//bob.regRequestForQueue(bob, TDS_QueueName_Reg);
		
	 // Step 2: 
		/*
		bob.getQueueNameFromTDS(TDS_QueueName_Reg, bob.getUUID());
		System.out.println(bob.getQueueName());
		bob.storeQueue(NAME, bob.getQueueName());
		*/
		
	// Step 3: 
		//receiveEOOMsg(bob);
		
    // Step 4: 
		//String eoo = bob.readline(NAME, "EOO");
		//sendEORMsg(bob.readline(NAME, "Queue"), bob.readline(NAME, "Label"), bob.getEOR(eoo), bob.getUUID().trim(), bob.readline(NAME, "Target"));
		
		
	// Step 5: 
		//receiveDocMsg(bob, bob.readline(NAME, "Queue"));
		
		
	// Step 6: 
		//String sigMsg =  bob.sigMsg("PublicKeyRequest");
		//System.out.println("Exchange Message Signture: " + sigMsg);
		//sendPubKeyRequest(TDS_QueueName, "PublicKeyRequest", sigMsg, bob.getUUID(), aliceUUID);
		
		
	// Step 7:
		//receivePubKeyResponse(bob, bob.readline(NAME, "Queue"));
		

		//String shared = bob.sharedSecret(bob.readline(NAME, "RecipientPublicKey"));
		//System.out.println(shared);
		
		//bob.decrypt("resource/" + NAME + "/recClassified", "resource/" + NAME + "/deClassified", shared);
		
		

	}

	
	
	/**
	 * 
	 * @param queueName
	 * @throws IOException 
	 */
	public static void receiveDocMsg(Client c, String queueName) throws IOException {
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
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            System.out.println("  DocumentName:" + attributes.get("DocumentName").getStringValue());
            document = attributes.get("Document").getBinaryValue().asReadOnlyBuffer();
            document.flip();
            
            
            c.replaceSelected(NAME, "Label", attributes.get("Label").getStringValue());
            c.replaceSelected(NAME, "Target", attributes.get("Target").getStringValue());
            
            
            OutputStream outputFile;
            WritableByteChannel outputChannel = null;
			try {
				outputFile = new FileOutputStream("resource/"+  NAME + "/recClassified");
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
	 * @param target
	 * @throws IOException 
	 */
	public static void receiveEOOMsg(Client target) throws IOException {
		String queue = target.readline(NAME, "Queue");
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
            
            target.replaceSelected(NAME, "Label", attributes.get("Label").getStringValue());
            target.replaceSelected(NAME, "Target", attributes.get("Source").getStringValue());
            target.replaceSelected(NAME, "EOO", getEOO());

        }		
	}
	
	/**
	 * 
	 * @param c
	 * @param q
	 * @param eoo
	 * @return
	 * @throws IOException 
	 */
	public static boolean sendEORMsg(String queue, String label, String eor, String uuid, String target) throws IOException {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		//String queue = q;
		//String label = source.getLabel();
		//String eor = source.getEOR(eoo);
		//String uuid = source.getUUID();
		//String target = source.getDestination();


		
		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (eor != null && !eor.isEmpty())
				&& (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			
			success = sqsx.sendMessage(queue, label, eor, uuid, target);
			if (!success)
				throw new IllegalArgumentException("null or empty value is passed");
		}
	
		return success;
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

	/**
	 * 
	 * @return
	 */
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
	
	
	
	public static void sendPubKeyRequest(String queueName, String protocol, String sigMsg, String source, String target) {
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        
        // Send an exchange request
        success = sqsx.exchangeRequest(queueName, protocol, sigMsg, source, target);
	}
	
	
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
            
          
    		c.replaceSelected(NAME, "RecipientPublicKey", c.getTargetPubKey());
            
            
            //success = sqsx.deleteMessage(myQueue, messageHandle);
            //System.out.println("Deleted message from queue " + myQueue + " " + success);
        }
	}
	

}
