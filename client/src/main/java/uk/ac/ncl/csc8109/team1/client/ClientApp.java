package uk.ac.ncl.csc8109.team1.client;

import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoApp;

import java.util.UUID;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 */
public class ClientApp {
	private static String Userid;
	private static String clientKey1;
	
	public ClientApp(){}
	
	public String getUserid() {
		return Userid;
	}
	public static String setUserid() {
		Userid = UUID.randomUUID().toString();
		return Userid;
	}
	
    public static void main( String[] args){
        System.out.println("ClientID: "+ setUserid());
        
        
    	Crypto client = new Crypto();
    	Crypto client1 = new Crypto();
    	Crypto client2 = new Crypto();
    	
    	
//    	clientKey = client.getPublicKey();
    	
    	File f = new File("src/main/resources/sample");
    	
    	clientKey(client, f);
//    	sender(client1, f);
//    	receiver(client2, f);
//    	
//		encryptionAndDecryption(client1, client2);
    	
    	
//    	 System.out.println("ClientKey: "+ clientKey);
    	
    }

	public static void clientKey(Crypto clientKey, File f) {
    	String clientKey1 = clientKey.getPublicKey();
    	String clientKey2 = clientKey.getPrivateKey();

    	
    	System.out.println("\nCreate Public Key for Client,"+"\nClient Public key: "+ clientKey1);
    	System.out.println("\nCreate Private Key for Client,"+"\nClient Private key: "+ clientKey2);
    	
    }
	
	CryptoApp EncDec = new CryptoApp();
	
	
    public static String getclientKey1(){
   		return clientKey1 ;
    }
    public String setclientKey1(String clientKey1){
    	return clientKey1;
    } 
    	
//    public static void getSharedKey(CryptoInterface sender, CryptoInterface receiver) {
//   		System.out.println("client1 computes the shared key");
//   		String senderCheck = sender.getSharedKey(receiver.getPublicKey());
//   		System.out.println(senderCheck);
//   		System.out.println("client2 computes the shared key");
//   		String receiverCheck = receiver.getSharedKey(sender.getPublicKey());
//   		System.out.println(receiverCheck);
//    	System.out.print("Both key matches: ");
//   		System.out.println(senderCheck.equals(receiverCheck));
//   		System.out.println();
//    }
//    	
//    public static void encryptionAndDecryption(CryptoInterface sender, CryptoInterface receiver) {
//   		String senderCheck = sender.getSharedKey(receiver.getPublicKey());
//   		String receiverCheck = receiver.getSharedKey(sender.getPublicKey());
//   		
//   		System.out.println("client1 encrypts the file using the computes shared key");
//		sender.encryptFile("src/main/resources/sample", "src/main/resources/encryptedsample", senderCheck);
//		System.out.println("client2 decrypts the file using the computes shared key");
//		receiver.decryptFile("src/main/resources/encryptedsample", "src/main/resources/decryptedsample", receiverCheck);
//    }
//    
//    public static void receiver(Crypto client2, File f) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public static void sender(Crypto client1, File f) {
//		// TODO Auto-generated method stub
//		
//	}
	


//	private static void sender(Crypto client) {
//		// TODO Auto-generated method stub
//		
//	}
//	public static String getPublicKey(Crypto client, File f) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

}
