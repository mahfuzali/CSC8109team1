package uk.ac.ncl.csc8109.team1.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {

	public static void main(String[] args) {
		File file = new File("sample");
		
		try {
			String hash = hashFile(file);
			System.out.println(hash);
		} catch (NoSuchAlgorithmException | IOException e) {
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
	private static String hashFile(File path) throws NoSuchAlgorithmException, IOException  {
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
     * @param <code>bytes</code> bytes of hash
     * @return hex representation of hash
     */
	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'a', 'b', 'c', 'd', 'e', 'f' };
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
	
	
}
