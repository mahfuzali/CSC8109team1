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


import java.io.IOException;
import java.util.Scanner;

import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;

/** 
 * This class shows how to use the client object
 * 
 * @author Mahfuz Ali
 * @Version 1.3
 * @email m.ali4@newcastle.ac.uk
 */
public class ClientExample {
	public static void main(String[] args) throws IOException, InterruptedException {
		CryptoInterface crypto = new Crypto();
		String bobUUID = "deae6d74-80cb-43dd-b913-c66e0dd8bb40";
		
		String sign = crypto.getSignature(bobUUID);
		System.out.println(sign);
		
		//Client c = new Client("name");
		/*c.setLabel("label6");
		c.setDestination("source7");
		c.setTds("tds1");
		
		System.out.println(c.getUUID());
		System.out.println(c.getPublicKey());
		System.out.println(c.getPrivateKey());
		
		System.out.println(c.getQueueName());
		System.out.println(c.getLabel());
		System.out.println(c.getTds());
		System.out.println(c.getSource());
		*/
		
		//String l = c.getTds() + "," +  c.getSource() + "," + c.getLabel();
		//System.out.println(l);		
		//String FILENAME = "teamPath";
		//c.writeToFile(FILENAME, l);
		//c.readFromFile("teamPath");
		
		/*
		while(c.getQueueName() == null) {
			c.regRequestForQueue(c, "");
			Thread.sleep(5000);
			c.getQueueNameFromTDS("", c.getUUID());
		}
		*/
		
		/*
		System.out.println("1. Request for queue name");
		System.out.println("2. Request for a exchange");
		System.out.println("3. Send a document with EOO");
		System.out.println("4. Get EOR from TDS");
		System.out.println("9. End program");
		*/
		
		String[] items = {"Register and request for queue name",
						  "Request for a exchange",
						  "Send a document with EOO",
						  "Get EOR from TDS",
						  "Send abort message",
						  "End program"};
		
		Scanner in = new Scanner(System.in);
        // print menu
        for (int i = 1; i <= 5; i++) {
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
                    break;
              case 2:
                    System.out.println("You've chosen item #2");
                    // do something...
                    break;
              case 3:
                    System.out.println("You've chosen item #3");
                    // do something...
                    break;
              case 4:
                    System.out.println("You've chosen item #4");
                    // do something...
                    break;
              case 5:
                    System.out.println("You've chosen item #5");
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
	
	
}
