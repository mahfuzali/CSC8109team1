package uk.ac.ncl.csc8109.team1.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

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

/**
 * Class containing all the necessary cryptography functionalities that are 
 * required to execute the fair-exchange protocol. The main method shows the way
 * in which methods should be used.  
 * 
 * @author Mahfuz Ali
 * @Version 1.0
 * @email m.ali4@newcastle.ac.uk
 */
public class Crypto {

	private static String savePath = System.getProperty("user.dir");

	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeySpecException {
		File file = new File("sample");

		String hash = hashFile(file);
		System.out.println(hash);

		KeyPair keypair = genKeyPair("DSA");
		// Save to file system
		saveKeyPair(savePath, keypair);
		// Loads from file system
		KeyPair loaded = loadKeyPair(savePath, "DSA");

		System.out.println(bytesToHex(loaded.getPublic().getEncoded()));
		System.out.println(bytesToHex(loaded.getPrivate().getEncoded()));

		try {
			byte[] digitalSignature = signData(hexToByte(hash), loaded.getPrivate());
			System.out.println(bytesToHex(digitalSignature));

			boolean verified = verifySig(hexToByte(hash), loaded.getPublic(), digitalSignature);
			System.out.println(verified);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Computes hash of a file
	 * 
	 * @param <code>path</code> file path
	 * @return Hash of file in hex representation
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private static String hashFile(File path) throws NoSuchAlgorithmException, IOException {
		@SuppressWarnings("resource")
		FileInputStream input = new FileInputStream(path);
		String algorithm = "SHA-256";

		MessageDigest digest = MessageDigest.getInstance(algorithm);

		byte[] bytesBuffer = new byte[1024];
		int bytesRead = -1;

		while ((bytesRead = input.read(bytesBuffer)) != -1) {
			digest.update(bytesBuffer, 0, bytesRead);
		}

		byte[] hashedBytes = digest.digest();

		return bytesToHex(hashedBytes);

	}

	/**
	 * Converts array of bytes to hex
	 * 
	 * @param <code>bytes</code> bytes of hash
	 * @return hex representation of hash
	 */
	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Converts hex to array of bytes
	 * 
	 * @param <code>hex</code> hex of hash
	 * @return byte representation of hash
	 */
	public static byte[] hexToByte(String hex) {
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(hex.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	/**
	 * Generates key pairs: public and private
	 * @param </code>algorithm</code> type of cryto algorithm 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static KeyPair genKeyPair(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithm);
		keyGenerator.initialize(1024, SecureRandom.getInstance("SHA1PRNG", "SUN"));
		return keyGenerator.generateKeyPair();
	}

	/**
	 * Stores the keypair 
	 * 
	 * @param <code>path</code> directory to save the keypair 
	 * @param <code>keyPair</code> keypair: public and private 
	 * @throws IOException
	 */
	public static void saveKeyPair(String path, KeyPair keyPair) throws IOException {
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
	 * Loads the key 
	 * @param <code>path</code> load from specified directory
	 * @param <code>algorithm</code> algorithm to use to convert KeyPair
	 * @return KeyPair: (public and private)
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static KeyPair loadKeyPair(String path, String algorithm)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
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
	 */
	public static byte[] signData(byte[] data, PrivateKey key)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sign = Signature.getInstance("SHA1withDSA");
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
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public static boolean verifySig(byte[] data, PublicKey key, byte[] sig)
			throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		Signature sign = Signature.getInstance("SHA1withDSA");
		sign.initVerify(key);
		sign.update(data);
		return sign.verify(sig);
	}

}
