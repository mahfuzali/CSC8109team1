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

import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;
import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

/** 
 * This class represents a client in a fair-exchange protocol
 * 
 * @author Mahfuz Ali
 * @Version 1.3
 * @email m.ali4@newcastle.ac.uk
 */
public class Client {
	private String uid;
	private String publicKey;
	private String privateKey;
	private String queueName;
	private String label;
	private CryptoInterface crypto;
	
	private String tds;
	private String destination;
	
	private String abort;
	
	private String sourcePubKey;
	private String targetPubKey;
	
	private String EOO;
	private String EOR;
	
	/**
	 * In instantiation, a unique id is generated; along with,
	 * public and private key and a queue name
	 * @param clientName
	 */
	/* For testing purpose client name is provided */
	public Client() {
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param clientName
	 * @throws IOException
	 */
	void initialize(/*String clientName*/) throws IOException {
	   //String FILENAME = "resource/" + clientName + "/UUID";
		String FILENAME = "UUID";

		
		File uuidFile = new File(FILENAME);
		// if file doesnt exists, then create it
		if (!uuidFile.exists()) {
			uuidFile.createNewFile();
			
			uid = UUID.randomUUID().toString();
			write(FILENAME, uid);
		} else {
			uid = new String(Files.readAllBytes(Paths.get(FILENAME))).trim();
		}
		
		//File pubKey = new File( "resource/" + clientName + "/public.key");
		//File priKey = new File( "resource/" + clientName + "/private.key");
		File pubKey = new File("public.key");
		File priKey = new File("private.key");
		
		if (pubKey.exists() && priKey.exists()) {
			//System.out.println("Exist");
			crypto = new Crypto();
			//crypto.loadKeyPair("resource/" + clientName);
			crypto.loadKeyPair("");

			this.publicKey = crypto.getPublicKey();
			this.privateKey = crypto.getPrivateKey();
		} else {
			//System.out.println("Does not exist");
			pubKey.createNewFile();
			priKey.createNewFile();
			
			crypto = new Crypto();
			//crypto.storeKeyPair("resource/" + clientName);
			crypto.storeKeyPair("");

			
			this.publicKey = crypto.getPublicKey();
			this.privateKey = crypto.getPrivateKey();
		}
	}
	
	/**
	 * Gets the unique uuid
	 * @return <code>uid</code>
	 */
	public String getUUID() {
		return uid;
	}

	/**
	 * Gets the public key
	 * @return <code>publicKey</code>
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * Gets the private key
	 * @return <code>privateKey</code>
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * Gets the queue name
	 * @return <code>queueName</code>
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Sets the queue
	 * @param queueName
	 */
	void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Gets the label
	 * @return <code>label</code>
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label for exchange
	 * @param <code>label</code> label for exchange
	 */
	void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the name of the tds 
	 * @return <code>tds</code> name of the tds
	 */
	public String getTds() {
		return tds;
	}

	/**
	 * Sets the value name of the tds
	 * @param <code>tds<code> name of tds
	 */
	void setTds(String tds) {
		this.tds = tds;
	}

	/**
	 * Gets the name of the destination
	 * @return <code>source</code> destination name
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Sets the name of the destination
	 * @param <code>source</code> destination name
	 */
	void setDestination(String destination) {
		this.destination = destination;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAbort() {
		return abort;
	}
	
	/**
	 * 
	 * @param abort
	 */
	void setAbort(String abort) {
		this.abort = abort;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getSourcePubKey() {
		return sourcePubKey;
	}

	/**
	 * 
	 * @param sourcePubKey
	 */
	void setSourcePubKey(String sourcePubKey) {
		this.sourcePubKey = sourcePubKey;
	}

	/**
	 * 
	 * @return
	 */
	public String getTargetPubKey() {
		return targetPubKey;
	}

	/**
	 * 
	 * @param key
	 */
	void setTargetPubKey(String key) {
		targetPubKey = key;
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
	public void setEOO(String eOO) {
		EOO = eOO;
	}

	/**
	 * 
	 * @return
	 */
	public String getEOR() {
		return EOR;
	}

	/**
	 * 
	 * @param eOR
	 */
	public void setEOR(String eOR) {
		EOR = eOR;
	}

	/**
	 * Writes to a file only if the data does not exists
	 * @param <code>FILENAME<code> name of the file 
	 * @param <code>data</code> data to write
	 */
	public void writeToFile(String FILENAME, String data) {
		if(check(FILENAME, data) == false) {
			write(FILENAME, data);
		} else {
			System.out.println("Data already exists");
		}
	}
	
	
	/**
	 * Reads from a file
	 * @param <code>FILENAME</code> file to read from
	 */
	public void readFromFile(String FILENAME) {
		BufferedReader br = null;
		FileReader fr = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(FILENAME));
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Writes to a file o
	 * @param <code>FILENAME<code> name of the file 
	 * @param <code>data</code> data to write
	 */
	private void write(String FILENAME, String data) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(FILENAME);
			// if file doesnt exists, then create it
			if (!file.exists())
				file.createNewFile();
			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(data);
			bw.newLine();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}	
	}
	
	/**
	 * Checks if the data exists in the file
	 * @param <code>FILENAME<code> name of the file 
	 * @param <code>data</code> data to check
	 * @return <code>true</code> if exists; else, <code>false</code>
	 */
	private boolean check(String FILENAME, String data) {
		boolean flag = false;
		BufferedReader br = null;
		FileReader fr = null;		
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(FILENAME));
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.equals(data)) {
					flag = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}			
		return flag;
	}

	/**
	 * 
	 * @param f
	 * @param inputPath
	 * @param outputPath
	 * @param key
	 */
	void encrypt(String inputPath, String outputPath, String key) {
		crypto.encryptFile(inputPath, outputPath, key);
	}
	
	/**
	 * 
	 * @param f
	 * @param inputPath
	 * @param outputPath
	 * @param key
	 */
	void decrypt(String inputPath, String outputPath, String key) {
		crypto.decryptFile(inputPath, outputPath, key);
	}
	
	/**
	 * 
	 * @param recipientsPublicKey
	 * @return
	 */
	String sharedSecret(String recipientsPublicKey) {
		return crypto.getSharedKey(recipientsPublicKey);
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	String generateEOO(File file) {
		return crypto.getSignature(crypto.getHashOfFile(file));
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	String generateEOR(String str) {
		return crypto.getSignature(str);
	}

	/**
	 * 
	 * @param c
	 * @param tdsQueue
	 */
	 void regRequestForQueue(Client c, String tdsQueue) {
		boolean success = false;
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
	
	    
	    // Create a queue
	    success = sqsx.create(tdsQueue);
        System.out.println("Created queue " + tdsQueue + " " + success);
        
        System.out.println("Source's UUID Length:" + c.getUUID().length());
        // Send a registration request
        success = sqsx.registerRequest(tdsQueue, c.getUUID(), c.getPublicKey());
        System.out.println("Sent registration request to queue " + queueName + " " + success);
        
        
	}
	
	/**
	 * 
	 * @param tdsQueue
	 * @param userid
	 */
	 void getQueueNameFromTDS(String tdsQueue, String userid) {
		boolean success = false;

		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
        String messageHandle = null;
        
        // Try to receive message for Bob
        Message message = sqsx.receiveMyMessage(tdsQueue, userid);
        System.out.println("There is " + (message==null ? "no" : "a") + " message for " + userid);
        
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + tdsQueue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Queue:" + attributes.get("Queue").getStringValue());
            setQueueName(attributes.get("Queue").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
            
        	success = sqsx.deleteMessage(tdsQueue, messageHandle);
        	System.out.println("Deleted registration request from queue " + tdsQueue + " " + success);
            
        }
        
	}
	
	/**
	 * 
	 * @param tdsQueueName
	 * @param label
	 * @param source
	 * @param target
	 * @return
	 */
	boolean abortRequest(String tdsQueueName, String label, String source, String target) {
		boolean success = false;
		String signAbort = sigMsg("AbortRequest");
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        // Send a message
		success = sqsx.abortRequest(tdsQueueName, label, signAbort, source, target);
        //success = sqsx.sendMessage(exchangeQueueName, label, "Abort", source, target);
        return success;
	}
	
	/**
	 * 
	 */
	boolean abortResponse(Client c, String myQueue) {

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
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());

            c.setAbort(message.getBody());

            
            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
		return success;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	String sigMsg(String s) {
		return crypto.getSignature(s);
	}
	
	/**
	 * 
	 * @param clientName
	 * @param queueName
	 */
	void storeQueue(String queueName) {
		//String FILENAME = "resource/" + clientName + "/queue";
		String FILENAME = "queue";

		
		File qNameFile = new File(FILENAME);
		if (!qNameFile.exists()) {
			try {
				qNameFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			write(FILENAME, queueName);
		}
	}
	
	/**
	 * 
	 * @param clientName
	 * @return
	 * @throws IOException
	 */
	String readQueueNameFromFile() throws IOException {
		String FILENAME = "queue";
		return new String(Files.readAllBytes(Paths.get(FILENAME)));
	}
	
	
	/**
	 * Replaces specfic line from key file
	 * 
	 * @param startofline
	 * @param data
	 * @param iv
	 * @throws IOException
	 */
	void replaceSelected(String startofline, String data) throws IOException {
		String store = "";
		try {
			File file = new File("record");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
				if (line.startsWith(startofline)) {
					store = line;
				}
			}
			reader.close();

			String[] tmp = store.split(" : ");

			String newtext = oldtext.replace(tmp[1], data);

			FileWriter writer = new FileWriter("record");
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
	/**
	 * Read a specific line
	 * 
	 * @param startofline
	 * @return
	 * @throws IOException
	 */
	String readline(String startofline) throws IOException {
        String a = "";
        	
        File newfile = new File("record");
        BufferedReader reader = new BufferedReader(new FileReader(newfile));
        String line = "";
        
        while((line = reader.readLine()) != null)
        {
            if(line.startsWith(startofline))
            	a = line;
        }
        
        reader.close();
        String[] tmp = a.split(" : ");

        return tmp[1];
	}
	
	/**
	 * 
	 * @param queueName
	 * @param label
	 * @param source
	 * @param target
	 */
	void returnLabelToTds(String queueName, String label, String source, String target) {
		String signlbl = sigMsg(label.replaceAll("-", ""));
		
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		// Send a message
        success = sqsx.sendMessage(queueName, label, signlbl, source, target);
        System.out.println("Sent message to queue " + queueName + " " + success);
	}
	
}
