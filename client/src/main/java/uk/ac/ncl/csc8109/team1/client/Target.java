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

import java.util.Map;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

public class Target {
	private static final String TDS_queueName = "csc8109_1_tds_queue_20070306";
	private static final String TDS_QueueName_Reg = "csc8109_1_tds_queue_20070306_reg";

	private static final String name = "bob-";
	
	private static String EOO;
	private static String EOR;

	public static void main(String[] args) {
		Client bob  = new Client();
		bob.setLabel("label1");
		bob.setSource("alice");

		
		bob.regRequest(bob, TDS_QueueName_Reg);
		
		/* Change the Bob to uuid */
		bob.getQueueNameFromTDS(TDS_QueueName_Reg, "Bob");
		System.out.println(bob.getQueueName());
		
		
		receiveMsg(bob);
		System.out.println(getEOO());
		
		sendMsg(bob, TDS_queueName, getEOO());	
	}

	/**
	 * 
	 * @param target
	 */
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
	
	/**
	 * 
	 * @param source
	 * @param q
	 * @param eoo
	 * @return
	 */
	public static boolean sendMsg(Client source, String q, String eoo) {
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

}
