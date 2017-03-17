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

/**
 * This class shows the way in which the {@link Crypto} class can be used. 
 * 
 * @author Mahfuz Ali
 * @Version 1.2
 * @email m.ali4@newcastle.ac.uk
 */
public class CryptoApp {

	public static void main(String[] args) {
		CryptoInterface alice = new Crypto();
		CryptoInterface bob = new Crypto();
		
		//alice.storeKeyPair("resource");
		alice.loadKeyPair("resource");
		
		
		
		
		sender(alice);
		receiver(bob);
		
		File f = new File("sample");
		getHash(alice, f);
		getSignature(alice, f);
		getVerification(alice, bob, f);
		
		getSharedKey(alice, bob);
		encryptionAndDecryption(alice, bob);
		/**/	
	}
	
	/**
	 * 
	 * @param sender
	 */
	public static void sender(CryptoInterface sender) {
		System.out.println("Create KeyPairs for Alice");
		System.out.print("Public key:\t");
		System.out.println(sender.getPublicKey());
		System.out.print("Private key:\t");
		System.out.println(sender.getPrivateKey());
		System.out.println();
	}

	/**
	 * 
	 * @param receiver
	 */
	public static void receiver(CryptoInterface receiver) {
		System.out.println("Create KeyPairs for Bob");
		System.out.print("Public key:\t");
		System.out.println(receiver.getPublicKey());
		System.out.print("Private key:\t");
		System.out.println(receiver.getPrivateKey());
		System.out.println();
	}
	
	/**
	 * 
	 * @param sender
	 * @param f
	 */
	public static void getHash(CryptoInterface sender, File f) {
		System.out.println("Alice generates hash of the file");
		String hash = sender.getHashOfFile(f);
		System.out.println(hash);
		System.out.println();
	}

	/**
	 * 
	 * @param sender
	 * @param f
	 */
	public static void getSignature(CryptoInterface sender, File f) {
		System.out.println("Alice generates signature of the hash");
		String hash = sender.getHashOfFile(f);
		String signature = sender.getSignature(hash);
		System.out.println(signature);
		System.out.println();
	}
	
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param f
	 */
	public static void getVerification(CryptoInterface sender, CryptoInterface receiver, File f) {
		System.out.println("Bob verifies the signature of the hash");
		String hash = sender.getHashOfFile(f);
		String signature = sender.getSignature(hash);
		String verification = receiver.isVerified(hash, sender.getPublicKey(), signature);
		System.out.println(verification);
		System.out.println();
	}
	
	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	public static void getSharedKey(CryptoInterface sender, CryptoInterface receiver) {
		System.out.println("Alice computes the shared key");
		String senderCheck = sender.getSharedKey(receiver.getPublicKey());
		System.out.println(senderCheck);
		System.out.println("Bob computes the shared key");
		String receiverCheck = receiver.getSharedKey(sender.getPublicKey());
		System.out.println(receiverCheck);
		System.out.print("Both key matches: ");
		System.out.println(senderCheck.equals(receiverCheck));
		System.out.println();
	}
	
	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	public static void encryptionAndDecryption(CryptoInterface sender, CryptoInterface receiver) {
		String senderCheck = sender.getSharedKey(receiver.getPublicKey());
		String receiverCheck = receiver.getSharedKey(sender.getPublicKey());
	
		System.out.println("Alice encrypts the file using the computes shared key");
		sender.encryptFile("resource/classified", "resource/encryptedclassified", senderCheck);
		System.out.println("Bob decrypts the file using the computes shared key");
		receiver.decryptFile("resource/encryptedclassified", "resource/decryptedclassified", receiverCheck);
	}
	
	
}
