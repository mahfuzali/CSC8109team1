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
 * @Version 1.5
 * @email m.ali4@newcastle.ac.uk
 */
public class Client {
	private CryptoInterface crypto;

	private String uuid;
	private String publicKey;
	private String privateKey;
	
	private String queueName;
	private String label;	
	
	private String tds;
	private String destination;
	private String abort;

	private String sourcePubKey;
	private String targetPubKey;
	
	private String EOO;
	private String EOR;
	
	private final String TDS_QUEUE = "csc8109_1_tds_queue_20070306";
	private final String TDS_REGISTRATION_QUEUE = "csc8109_1_tds_queue_20070306_reg";
	private final String  COFFEY_SAIDHA_PROTOCOL = "CoffeySaidha";

	
	private boolean receivedDoc;
	
	/**
	 * In instantiation, a unique id is generated; along with,
	 * public and private key and a queue name
	 */
	public Client() {
		try {
			initialise();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Initialises the UUID, public and private key; 
	 * if already exists then loads it from files
	 * 
	 * @throws IOException
	 */
	void initialise() throws IOException {
		String FILENAME = "UUID";

		File uuidFile = new File(FILENAME);
		
		// if file doesnt exists, then create it
		if (!uuidFile.exists()) {
			uuidFile.createNewFile();
			
			uuid = UUID.randomUUID().toString();
			write(FILENAME, uuid);
		} else {
			uuid = new String(Files.readAllBytes(Paths.get(FILENAME))).trim();
		}
		
		File pubKey = new File("public.key");
		File priKey = new File("private.key");
		
		if (pubKey.exists() && priKey.exists()) {
			crypto = new Crypto();
			crypto.loadKeyPair("");

			this.publicKey = crypto.getPublicKey();
			this.privateKey = crypto.getPrivateKey();
		} else {
			pubKey.createNewFile();
			priKey.createNewFile();
			
			crypto = new Crypto();
			crypto.storeKeyPair("");
			
			this.publicKey = crypto.getPublicKey();
			this.privateKey = crypto.getPrivateKey();
		}
	}
	
	/**
	 * Gets the unique uuid
	 * 
	 * @return <code>uuid</code> UUID
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * Gets the public key
	 * 
	 * @return <code>publicKey</code> public key
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * Gets the private key
	 * 
	 * @return <code>privateKey</code> private key
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * Gets the queue name
	 * 
	 * @return <code>queueName</code> queue name
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Sets the queue
	 * 
	 * @param <code>queueName</code>  queue name
	 */
	void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Gets the label
	 * 
	 * @return <code>label</code> exchange label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label for exchange
	 * 
	 * @param <code>label</code> exchange label
	 */
	void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the name of the tds 
	 * 
	 * @return <code>tds</code> name of the tds
	 */
	public String getTds() {
		return tds;
	}

	/**
	 * Sets the value name of the tds
	 * 
	 * @param <code>tds<code> name of tds
	 */
	void setTds(String tds) {
		this.tds = tds;
	}

	/**
	 * Gets the name of the destination
	 * 
	 * @return <code>source</code> destination name
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Sets the name of the destination
	 * 
	 * @param <code>source</code> destination name
	 */
	void setDestination(String destination) {
		this.destination = destination;
	}
	
	/**
	 * Gets abort response
	 * 
	 * @return <code>abort</code> abort response
	 */
	public String getAbort() {
		return abort;
	}
	
	/**
	 * Sets abort response 
	 * 
	 * @param <code>abort</code> abort response
	 */
	void setAbort(String abort) {
		this.abort = abort;
	}

	
	/**
	 * Gets source's public key
	 * 
	 * @return <code>sourcePubKey</code> source's public key
	 */
	public String getSourcePubKey() {
		return sourcePubKey;
	}

	/**
	 * Sets source's public key
	 * 
	 * @param <code>sourcePubKey</code> source's public key
	 */
	void setSourcePubKey(String sourcePubKey) {
		this.sourcePubKey = sourcePubKey;
	}

	/**
	 * Gets target's public key
	 * 
	 * @return <code>targetPubKey</code> target's public key
	 */
	public String getTargetPubKey() {
		return targetPubKey;
	}

	/**
	 * Sets target's public key
	 * 
	 * @param <code>targetPubKey</code> target's public key
	 */
	void setTargetPubKey(String targetPubKey) {
		this.targetPubKey = targetPubKey;
	}

	/**
	 * Gets the EOO
	 * 
	 * @return <code>EOO</code> EOO from source
	 */
	public String getEOO() {
		return EOO;
	}

	/**
	 * Sets the EOO
	 * 
	 * @param <code>eOO</code> EOO from source
	 */
	public void setEOO(String eOO) {
		EOO = eOO;
	}

	/**
	 * Gets the EOR
	 * 
	 * @return <code>EOR</code> EOO from target
	 */
	public String getEOR() {
		return EOR;
	}

	/**
	 * Gets the EOR
	 * 
	 * @param <code>eOR</code> EOO from target
	 */
	public void setEOR(String eOR) {
		EOR = eOR;
	}

	/**
	 * Check if document received
	 * 
	 * @return <code>receivedDoc</code> true if received; otherwise, false
	 */
	public boolean isReceivedDoc() {
		return receivedDoc;
	}

	/**
	 * Sets value when expecting a document from TDS
	 * 
	 * @param <code>receivedDoc</code> true if received; otherwise, false
	 */
	public void setReceivedDoc(boolean receivedDoc) {
		this.receivedDoc = receivedDoc;
	}

	/**
	 * Writes to a file only if the data does not exists
	 * 
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
	 * 
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
	 * Writes to a file
	 * 
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
	 * 
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
	 * Encrypts the file
	 * 
	 * @param <code>inputPath</code> file to encrypt
	 * @param <code>outputPath<code> encrypted file
	 * @param <code>key</code> key to encrypt the file with
	 */
	void encrypt(String inputPath, String outputPath, String key) {
		crypto.encryptFile(inputPath, outputPath, key);
	}
	
	/**
	 * Decryptd the file
	 * 
	 * @param <code>inputPath</code> file to decrypt
	 * @param <code>outputPath<code> decrypted file
	 * @param <code>key</code> key to decrypt the file with
	 */
	void decrypt(String inputPath, String outputPath, String key) {
		crypto.decryptFile(inputPath, outputPath, key);
	}
	
	/**
	 * Calculates shared secret
	 * 
	 * @param <code>targetPublicKey</code> target's public key
	 * @return
	 */
	String sharedSecret(String targetPublicKey) {
		return crypto.getSharedKey(targetPublicKey);
	}
	
	/**
	 * Generates EOO
	 * 
	 * @param <code>file</code> file to be used to generate EOO
	 * @return <code>EOO</code> generated EOO
	 */
	String generateEOO(File file) {
		return crypto.getSignature(crypto.getHashOfFile(file));
	}

	/**
	 * Generates EOR
	 * 
	 * @param <code>str</code> EOO to be used to generate EOR
	 * @return <code>EOR</code> generated EO
	 */
	String generateEOR(String eoo) {
		return crypto.getSignature(eoo);
	}

	/**
	 * Generates a signature of a string
	 * 
	 * @param <code>str</code> string to be used to generate signature
	 * @return <code>signature</code> generated signature
	 */
	String sigMsg(String str) {
		return crypto.getSignature(str);
	}
	
	/**
	 * Register with TDS and receive a private queue name
	 * 
	 * @param </code>c</code> client
	 * @param <code>tdsQueue</code> TDS queue
	 */
	 void regRequestForQueue(Client client, String tdsQueue) {
		boolean success = false;
		
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
	
	    
	    // Create a queue
	    success = sqsx.create(tdsQueue);
        System.out.println("Created queue " + tdsQueue + " " + success);
        
        System.out.println("Source's UUID Length:" + client.getUUID().length());
        // Send a registration request
        success = sqsx.registerRequest(tdsQueue, client.getUUID(), client.getPublicKey());
        System.out.println("Sent registration request to queue " + queueName + " " + success);
        
        
	}
	
	/**
	 * Gets the queue name from the TDS
	 * 
	 * @param <code>tdsQueue</code> TDS queue
	 * @param <code>uuid</code> client UUID
	 */
	 void getQueueNameFromTDS(String tdsQueue, String uuid) {
		boolean success = false;

		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		System.out.println("Initialised queue service");
        String messageHandle = null;
        
        // Try to receive message for Bob
        Message message = sqsx.receiveMyMessage(tdsQueue, uuid);
        System.out.println("There is " + (message==null ? "no" : "a") + " message for " + uuid);
        
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
	 * Sends abort request to TDS
	 * 
	 * @param <code>tdsQueue</code> TDS queue
	 * @param </code>label</code> exchange label
	 * @param <code>source</code> sending client
	 * @param <code>target</code> receiving client
	 * @return <code>true</code> if abort sent; otherwise, <code>false</code>
	 */
	boolean abortRequest(String tdsQueue, String label, String source, String target) {
		boolean success = false;
		String signAbort = sigMsg("AbortRequest");
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
        // Send a message
		success = sqsx.abortRequest(tdsQueue, label, signAbort, source, target);
        //success = sqsx.sendMessage(exchangeQueueName, label, "Abort", source, target);
        return success;
	}
	
	/**
	 * Receives abort response from TDS
	 *
	 * @param <code>client</code> client
	 * @param <code>myQueue</code> client's queue
	 * @return <code>true</code> if abort accepted; if denied, <code>false</code>
	 */
	boolean abortResponse(Client client, String myQueue) {

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

            client.setAbort(message.getBody());

            success = sqsx.deleteMessage(queueName, messageHandle);
            System.out.println("Deleted message from queue " + queueName + " " + success);
        }
		return success;
	}
	
	/**
	 * Sends label back to TDS
	 * 
	 * @param <code>tdsQueue</code> TDS queue
	 * @param </code>label</code> exchange label
	 * @param <code>source</code> sending client
	 * @param <code>target</code> receiving client
	 */
	void returnLabelToTds(String tdsQueue, String label, String source, String target) {
		String signlbl = sigMsg(label.replaceAll("-", ""));
		
		boolean success = false;
		// Initialise queue service
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		// Send a message
        success = sqsx.sendMessage(tdsQueue, label, signlbl, source, target);
        System.out.println("Sent message to queue " + tdsQueue + " " + success);
	}
	
	/**
	 * Reads queue name from a file 
	 * 
	 * @return queue name 
	 * @throws IOException
	 */
	String readQueueNameFromFile() throws IOException {
		String FILENAME = "queue";
		return new String(Files.readAllBytes(Paths.get(FILENAME)));
	}
	
	
	/**
	 * Replaces specfic line from key file
	 * 
	 * @param <code>startofline</code> start of line
	 * @param <code>data</code> data to be replaced with
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
	 * @param <code>startofline</code> start of line
	 * @return second element of the line 
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
	 * Gets the tds queue
	 * 
	 * @return <code>TDS_QUEUE</code> tds queue
	 */
	public String getTdsQueue() {
		return TDS_QUEUE;
	}

	/**
	 * Gets the tds registration queue
	 * 
	 * @return <code>TDS_REGISTRATION_QUEUE</code> tds registration queue
	 */
	public String getTdsRegistrationQueue() {
		return TDS_REGISTRATION_QUEUE;
	}

	/**
	 * Gets Coffey-Saidha protocol
	 * 
	 * @return <code>COFFEY_SAIDHA_PROTOCOL</code> Coffey-Saidha protocol 
	 */
	public String getCoffeySaidhaProtocol() {
		return COFFEY_SAIDHA_PROTOCOL;
	}

}
