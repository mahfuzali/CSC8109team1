package uk.ac.ncl.csc8109.team1.client;

import java.io.File;

import uk.ac.ncl.csc8109.team1.crypto.Crypto;
import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;

public class Source {
	//**Starts exchange**//
	public static void main(String[] args) {
		Client source  = new Client();
		
	
	
	//**Request exchange from TDS, which is label(id) of exchange**//
	
	
	//**Wait for exchange id from TDS**//
	
	
	//**Send message1 to TDS
	//doc along with signature of A containing hash of doc (doc, sigA(h(doc)))**//
		File f = new File("src/main/java/sample");
	
		CryptoInterface a = new Crypto();
		
		String hash = a.getHashOfFile(f);
		System.out.println(hash);
		
		//String signature = a.getSignature();
		//System.out.println(sig);
		
	
	//**wait for message(Evidence of origin) from TDS at the of exchange
	//which is signature of B containing signature of A which contains hash of the doc(sigB(sigA(h(doc)) **//
	
	
	//**Output then end**//
		
	}
	

}
