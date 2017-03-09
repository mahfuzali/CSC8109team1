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
import uk.ac.ncl.csc8109.team1.crypto.Crypto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

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
	private String source;
	
	/**
	 * 
	 */
	public Client() {
		uid = UUID.randomUUID().toString();
		crypto = new Crypto();
		this.publicKey = crypto.getPublicKey();
		this.privateKey = crypto.getPrivateKey();
		this.queueName = UUID.randomUUID().toString() + "_queue";
	}

	/**
	 * 
	 * @return
	 */
	public String getUUID() {
		return uid;
	}

	/**
	 * 
	 * @return
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * 
	 * @return
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * 
	 * @return
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return
	 */
	public String getTds() {
		return tds;
	}

	/**
	 * 
	 * @param tds
	 */
	public void setTds(String tds) {
		this.tds = tds;
	}

	/**
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * 
	 * @param source
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * 
	 * @param FILENAME
	 * @param data
	 */
	public void writeToFile(String FILENAME, String data) {
		if(check(FILENAME, data) == false) {
			write(FILENAME, data);
		} else {
			System.out.println("Data already exists");
		}
	}
	
	/**
	 * 
	 * @param FILENAME
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
	 * 
	 * @param FILENAME
	 * @param data
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
	 * 
	 * @param FILENAME
	 * @param data
	 * @return
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

}
