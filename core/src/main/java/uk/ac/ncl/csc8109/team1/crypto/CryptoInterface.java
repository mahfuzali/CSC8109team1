package uk.ac.ncl.csc8109.team1.crypto;

import java.io.File;

/**
 * An interface for a cryto
 * 
 * @author Mahfuz Ali
 * @Version 1.2
 * @email m.ali4@newcastle.ac.uk
 */
public interface CryptoInterface {

	/**
	 * Encrypts the file
	 * @param <code>inputPath</code> file to encrypt
	 * @param <code>outputPath<code> encrypted file
	 * @param <code>key</code> key to encrypt the file with
	 */
	void encryptFile(String inputPath, String outputPath, String key);
	
	/**
	 * Decryptd the file
	 * @param <code>inputPath</code> file to decrypt
	 * @param <code>outputPath<code> decrypted file
	 * @param <code>key</code> key to decrypt the file with
	 */
	void decryptFile(String inputPath, String outputPath, String key);
	
	/**
	 * Gets the public key
	 * @return publicKey
	 */
	String getPublicKey();
	
	/**
	 * Gets the private key
	 * @return private key
	 */
	String getPrivateKey();
	
	/**
	 * Gets the computed shared key 
	 * @param receiverPublicKey
	 * @return
	 */
	String getSharedKey(String receiverPublicKey);
	
	/**
	 * Gets the hash of the file 
	 * @return hash of file
	 */
	String getHashOfFile(File file);
	
	/**
	 * Sets and gets the signature of a given hash value.
	 * Can be used to generate the EOO and EOR part of
	 * the fair-exchange protocol
	 * @param <code>hash</code> hash value to be signed
	 */
	 String getSignature(String hash);
	
	/**
	 * Verifies the signauture of the hash.
	 * @param <code>hash</code> hash value signed
	 * @param <code>publicKey</code> public key of the signed party
	 * @param <code>signature</code> the original signature
	 * @return verified: if successful; otherwise, not verified
	 */
	String isVerified(String hash, String publicKey, String signature); 
	
	/**
	 * Stores the keypair in the local file system
	 * @param <code>path<code> file to store the keypair
	 */
	public void storeKeyPair(String path);
	
	/**
	 * Loads the keypair from a file 
	 * @param <code>path<code> file where the keypair is stored
	 */
	public void loadKeyPair(String path);
}
