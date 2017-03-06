package uk.ac.ncl.csc8109.team1.client;

import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


public class Client {
	private String uid;
	private String publicKey;
	private String privateKey;
	private String queueName;
	private String label;
	private CryptoInterface crypto;
	
	private String tds;
	private String source;
	
	
	public Client() {
		uid = UUID.randomUUID().toString();
		crypto = new Crypto();
		this.publicKey = crypto.getPublicKey();
		this.privateKey = crypto.getPrivateKey();
		this.queueName = UUID.randomUUID().toString() + "_queue";
	}

	public String getUUID() {
		return uid;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTds() {
		return tds;
	}

	public void setTds(String tds) {
		this.tds = tds;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
	public void writeToFile(String filePath, String tds, String source, String label){
		// Creating a new file
	    Path newFile = Paths.get(filePath);
	    try {
	      Files.deleteIfExists(newFile);
	      newFile = Files.createFile(newFile);
	    } catch (IOException ex) {
	      System.out.println("Error creating file");
	    }
	    System.out.println(Files.exists(newFile));

	    //Writing to file
	    try(BufferedWriter writer = Files.newBufferedWriter(
	            newFile, Charset.defaultCharset())){
	      writer.append(tds + "," +  source + "," + label);
	      writer.newLine();
	      writer.flush();
	    } catch(IOException exception){
	      System.out.println("Error writing to file");
	    }
	}
	
	
	public void readFromFile(String filePath){
		//Reading from file
	    Path newFile = Paths.get(filePath);
	    try(BufferedReader reader = Files.newBufferedReader(
	    		newFile, Charset.defaultCharset())){
	      String lineFromFile = "";
	      System.out.println("\nThe contents of file are: ");
	      while((lineFromFile = reader.readLine()) != null){
	        System.out.println(lineFromFile);
	      }

	    } catch(IOException exception){
	      System.out.println("Error while reading file");
	    }
		
	}
}
