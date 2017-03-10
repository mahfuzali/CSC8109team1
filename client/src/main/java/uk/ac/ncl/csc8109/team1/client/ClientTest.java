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
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;

/** 
 * This class shows how to use the client object
 * 
 * @author Mahfuz Ali
 * @Version 1.3
 * @email m.ali4@newcastle.ac.uk
 */
public class ClientTest {
	public static void main(String[] args) {
		Client c = new Client();
		c.setLabel("label6");
		c.setSource("source7");
		c.setTds("tds1");
		
		System.out.println(c.getUUID());
		System.out.println(c.getPublicKey());
		System.out.println(c.getPrivateKey());
		System.out.println(c.getQueueName());
		
		System.out.println(c.getLabel());
		System.out.println(c.getTds());
		System.out.println(c.getSource());
		
		String l = c.getTds() + "," +  c.getSource() + "," + c.getLabel();
				
		String FILENAME = "teamPath";
		//c.writeToFile(FILENAME, l);
		//c.readFromFile("teamPath");

		
		
		
		
		
		boolean success = false;
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
	
		// Create a message queue name
	    String queueName = "csc8109_1_tds_queue_20070306_reg";
	    
	    // Create a queue
	    success = sqsx.create(queueName);
        System.out.println("Created queue " + queueName + " " + success);
        
        // Send a registration request
        success = sqsx.registerRequest(queueName, c.getUUID(), c.getPublicKey());
        System.out.println("Sent registration request to queue " + queueName + " " + success);
        
        
        
        // Receive registration request
        String messageHandle = null;
        Message message = sqsx.receiveMessage(queueName);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queueName);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Userid:" + attributes.get("Userid").getStringValue());
        }
        
		
		
		
		
	}
	
	
}
