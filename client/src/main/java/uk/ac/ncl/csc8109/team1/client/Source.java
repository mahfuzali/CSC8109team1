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
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;;

public class Source {
	@SuppressWarnings("unused")
	private static final String TDS_queueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";

	private static final String name = "alice-";
	private static String EOO;
	
	private static String queue; 
	
	public static void main(String[] args) throws IOException {
		Client alice = new Client();
		// For monday - tds should give me a label back
		alice.setLabel("label1");
		alice.setDestination("bob");

		alice.regRequest(alice, TDS_QueueName_Reg);
		
		/* Change the Alice to uuid */
		alice.getQueueNameFromTDS(TDS_QueueName_Reg, alice.getUUID());
		System.out.println(alice.getQueueName());
		
		/* Exchange record between multiple clients */
		String data = alice.getQueueName() + "," + alice.getLabel() + "," + alice.getDestination(); 
		alice.writeToFile("resource/"+ alice.getUUID() + "-exchange", data);
		
		
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
			success = sqsx.sendMsgDocument(queue, label, eoo, f.getPath(), name + uuid, target);
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
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
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
