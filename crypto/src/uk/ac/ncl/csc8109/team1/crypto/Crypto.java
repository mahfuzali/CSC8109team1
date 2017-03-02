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

package uk.ac.ncl.csc8109.team1.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.NoSuchPaddingException;

/** 
 * This class acts as a bridge between the cryptographic funcationalities
 * that were implemented in {@link CryptoUtil} class and the outside world. 
 * {@link CryptoApp} class shows the way in which this class may be used. 
 * 
 * @author Mahfuz Ali
 * @Version 1.2
 * @email m.ali4@newcastle.ac.uk
 */
public class Crypto {

	private KeyPair keypair;
	private String publicKey;
	private String privateKey;
	private String signature;
	private String hashOfFile;
	private boolean verified;
	
	private CryptoUtil c;

	/**
	 * KeyPair is generated at instantiation of class 
	 */
	public Crypto() {
		c = new CryptoUtil();
		try {
			keypair = c.genKeyPair();
			setKeypair(keypair);
			setPublicKey(byteToStr(keypair.getPublic().getEncoded()));
			setPrivateKey(byteToStr(keypair.getPrivate().getEncoded()));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the KeyPair: public and private
	 * @return <code<KeyPair</code> public and private
	 */
	public KeyPair getKeypair() {
		return keypair;
	}
	
	/**
	 * Sets the keypair: public and private key
	 * @param <code>keypair</code> public and private key
	 */
	void setKeypair(KeyPair keypair) {
		this.keypair = keypair;
	}
	
	/**
	 * Gets the public key
	 * @return publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}
	
	/**
	 * Sets the public key
	 * @param publicKey
	 */
	void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	/**
	 * Gets the private key
	 * @return
	 */
	public String getPrivateKey() {
		return privateKey;
	}
	
	/**
	 * Sets the private key
	 * @param privateKey
	 */
	void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
	/**
	 * Gets the hash of the file 
	 * @return hash of file
	 */
	public String getHashOfFile(File file) {
		try {
			return c.hashFile(file);
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashOfFile;
	}
		
	/**
	 * Gets the signature
	 * @return signature
	 */
	public String getSignature() {
		return signature;
	}
	
	/**
	 * Sets the signature of a given hash value.
	 * Can be used to generate the EOO and EOR part of
	 * the fair-exchange protocol
	 * @param <code>hash</code> hash value to be signed
	 */
	void setSignature(String hash) {
		try {
			this.signature = byteToStr(c.signData(strToByte(hash), strToPrivateKey(getPrivateKey())));
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Verifies the signauture of the hash.
	 * @param <code>hash</code> hash value signed
	 * @param <code>publicKey</code> public key of the signed party
	 * @param <code>signature</code> the original signature
	 * @return verified: if successful; otherwise, not verified
	 */
	public String isVerification(String hash, String publicKey, String signature) {
		try {
			verified = c.verifySig(strToByte(hash), strToPublicKey(publicKey), strToByte(signature));
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (verified)
			return "Verified";
		else
			return "Not Verified";
	}
	
	
	/**
	 * Encrypts the file
	 * @param <code>inputPath</code> file to encrypt
	 * @param <code>outputPath<code> encrypted file
	 * @param <code>key</code> key to encrypt the file with
	 */
	public void encryptFile(String inputPath, String outputPath, String key) {
		try {
			c.encrypt(new FileInputStream(inputPath), new FileOutputStream(outputPath), strToByte(key));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Decryptd the file
	 * @param <code>inputPath</code> file to decrypt
	 * @param <code>outputPath<code> decrypted file
	 * @param <code>key</code> key to decrypt the file with
	 */
	public void decryptFile(String inputPath, String outputPath, String key) {
		// TODO Auto-generated method stub
		try {
			c.decrypt(new FileInputStream(inputPath), new FileOutputStream(outputPath), strToByte(key));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	/**
	 * Gets the computed shared key 
	 * @param receiverPublicKey
	 * @return
	 */
	public String getSharedKey(String receiverPublicKey) {
		String secret = null;
		try {
			secret =  byteToStr(c.computeSharedSecret(strToByte(receiverPublicKey), strToPrivateKey(getPrivateKey())));
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return secret;
	}
	
	/**
	 * Stores the keypair in the local file system
	 * @param <code>path<code> file to store the keypair
	 */
	public void storeKeyPair(String path) {
		try {
			c.saveKeyPair(path, keypair);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the keypair from a file 
	 * @param <code>path<code> file where the keypair is stored
	 */
	public void loadKeyPair(String path) {
		 try {
			KeyPair pair = c.loadKeyPair(path);
			setPublicKey(byteToStr(pair.getPublic().getEncoded()));
			setPrivateKey(byteToStr(pair.getPrivate().getEncoded()));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	/**
	 * Gets the public key from the string 
	 * @param publicKey
	 * @return
	 */
	private PublicKey strToPublicKey(String publicKey) {
		try {
			return c.convertStrToPublicKey(publicKey);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the private key from the string
	 * @param privateKey
	 * @return
	 */
	private PrivateKey strToPrivateKey(String privateKey) {
		try {
			return c.convertStrToPrivateKey(privateKey);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts array of bytes to string
	 * @param <code>b</code> array bytes
	 * @return string
	 */
	private static String byteToStr(byte[] b) {
		return Base64.getEncoder().encodeToString(b);
	}
	
	/**
	 * Converts array of bytes to string
	 * @param <code>b</code> array bytes
	 * @return string
	 */
	private static byte[] strToByte(String s) {
		return Base64.getDecoder().decode(s);
		
	}

}
