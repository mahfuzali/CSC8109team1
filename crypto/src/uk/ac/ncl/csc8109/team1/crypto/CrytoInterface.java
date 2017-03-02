package uk.ac.ncl.csc8109.team1.crypto;

public interface CrytoInterface {

	void encryptFile(String inputPath, String outputPath, String key);
	
	void decryptFile(String inputPath, String outputPath, String key);
	
	String getPublicKey();
	
	String getPrivateKey();
	
	String getSharedSecret();
	
	String getHashOfFile();
	
	String getSignature();
	
	boolean getVerification(); 
}
