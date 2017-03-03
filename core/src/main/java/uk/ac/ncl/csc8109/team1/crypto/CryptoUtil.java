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
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class contains the implementations of all the necessary cryptography functionalities 
 * that are required to execute the fair-exchange protocol. Along with some auxilary 
 * functions. The {@link Crypto} class shows the way in which this class can/should be used.  
 * 
 * @author Mahfuz Ali
 * @Version 1.2
 * @email m.ali4@newcastle.ac.uk
 */
class CryptoUtil {

	private static String savePath = System.getProperty("user.dir");

	/**
	 * Encrypts a file
	 * 
	 * @param <code>input</code> file to encrypt 
	 * @param <code>output</code> encryted file
	 * @param <code>key</code> key to encrypt the file with
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	 void encrypt(InputStream input, OutputStream output, byte[] key) 
			throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException  {
		
		SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
		byte[] iv = new byte[16]; 
		r.nextBytes(iv);
		
		output.write(iv); 
		output.flush();

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);    	

		output = new CipherOutputStream(output, cipher);
		
		byte[] buf = new byte[1024];
		int numRead = 0;
		while ((numRead = input.read(buf)) >= 0) {
			output.write(buf, 0, numRead);
		}
		
		output.close();
	}
	
	/**
	 * Decrypts a file
	 * 
	 * @param <code>input</code> file to decrypt 
	 * @param <code>output</code> decryted file
	 * @param <code>key</code> key to decrypt the file with
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 */
	 void decrypt(InputStream input, OutputStream output, byte[] key) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
		byte[] iv = new byte[16]; 
		r.nextBytes(iv);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		input = new CipherInputStream(input, cipher);
		
		byte[] buf = new byte[1024];
		int numRead = 0;
		while ((numRead = input.read(buf)) >= 0) {
			output.write(buf, 0, numRead);
		}
		
		output.close();
	}
	
	/**
	 * Generates keypair: public and private
	 * 
	 * @param </code>algorithm</code> type of cryto algorithm 
	 * 
	 * @return KeyPair
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException 
	 */
	 KeyPair genKeyPair() 
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
		ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
		kpg.initialize(ecsp, r);
		return kpg.genKeyPair();
	}
	
	/**
	 * Computes shared secret key
	 * 
	 * @param <code>recPuKey</code> Recipient's public key
	 * @param <code>localPrKey</code> Sender's private key
	 * @return computed shared secret
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 */
	byte[] computeSharedSecret(byte[] recPuKey, PrivateKey localPrKey) 
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		 
		// Convert Received Byte Array into PublicKey
		PublicKey dhpk = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(recPuKey));
		 
		// Generate Shared Secret Using Local Private and Received Public
		ka.init(localPrKey);
		ka.doPhase(dhpk, true);
		 
		// Generate an SHA-256 Hash and Truncate to 128 Bits
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

		return Arrays.copyOfRange(sha256.digest(ka.generateSecret()), 0, 16);
	}
	

	/**
	 * Stores the keypair 
	 * 
	 * @param <code>path</code> directory to save the keypair 
	 * @param <code>keyPair</code> keypair: public and private 
	 * @throws IOException
	 */
	void saveKeyPair(String path, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();

		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		fos = new FileOutputStream(path + "/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}

	/**
	 * Loads the keypair from file system
	 *  
	 * @param <code>path</code> load from specified directory
	 * @return KeyPair: (public and private)
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException 
	 */
	KeyPair loadKeyPair(String path)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		// read public key from file
		File filePublicKey = new File(path + "/public.key");
		FileInputStream fis = new FileInputStream(filePublicKey);
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		// read private key from file
		File filePrivateKey = new File(path + "/private.key");
		fis = new FileInputStream(filePrivateKey);
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		// Convert them into KeyPair
		KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
	}
	
	/**
	 * Converts string to public key 
	 * @param <code>publicKey</code> public key in string
	 * @return public key
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeySpecException
	 */
	PublicKey convertStrToPublicKey(String publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] encodedPublicKey = strToByte(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		return keyFactory.generatePublic(publicKeySpec);
	}
	
	/**
	 * Converts string to private key
	 * @param <code>privateKey</code> private key in string 
	 * @return private key
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeySpecException
	 */
	PrivateKey convertStrToPrivateKey(String privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] encodedPrivateKey = strToByte(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		return keyFactory.generatePrivate(privateKeySpec); 
	}
	
	/**
	 * Computes hash of a file
	 * 
	 * @param <code>path</code> file path
	 * @return Hash of file in hex representation
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	  String hashFile(File path) throws NoSuchAlgorithmException, IOException {
		@SuppressWarnings("resource")
		FileInputStream input = new FileInputStream(path);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		byte[] bytesBuffer = new byte[1024];
		int bytesRead = -1;

		while ((bytesRead = input.read(bytesBuffer)) != -1) {
			digest.update(bytesBuffer, 0, bytesRead);
		}

		byte[] hashedBytes = digest.digest();

		return byteToStr(hashedBytes);
	}
	
	/**
	 * Signs the data
	 * 
	 * @param <code>data</code> data to sign
	 * @param <code>key</code> private key used to sign
	 * @return signed data
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws NoSuchProviderException 
	 */
	 byte[] signData(byte[] data, PrivateKey key)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
		Signature sign = Signature.getInstance("SHA256withECDSA", "SunEC");
		sign.initSign(key);
		sign.update(data);
		return sign.sign();
	}

	/**
	 * Verifies the data
	 * 
	 * @param </code>data</code> sign data
	 * @param <code>key</code> public key to verify the signature
	 * @param <code>sig</code> original sign data
	 * @return true: if verified; otherwise, false
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException 
	 */
	 boolean verifySig(byte[] data, PublicKey key, byte[] sig)
			throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
		Signature sign = Signature.getInstance("SHA256withECDSA", "SunEC");
		sign.initVerify(key);
		sign.update(data);
		return sign.verify(sig);
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
