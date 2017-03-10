package uk.ac.ncl.csc8109.team1.crypto;


import uk.ac.ncl.csc8109.team1.*;

import static org.junit.Assert.*;

import java.io.File;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;




public class CryptoTest {
	CryptoApp a = new CryptoApp();
	
	CryptoInterface alice = new Crypto();
	CryptoInterface bob = new Crypto();
	File f = new File("sample");
	
	
	@Test
	public void test1() {
		String aPbk = alice.getPublicKey();
		String aPrk = alice.getPrivateKey();
		String ahash = alice.getHashOfFile(f);
		String asignature = alice.getSignature(ahash);		

		String bPbk = bob.getPublicKey();
		String bPrk = bob.getPrivateKey();
		String bhash = bob.getHashOfFile(f);

		//String bsharedKey = bob.getSharedKey(bPbk);
		//String bsignature = bob.getSignature(bhash);	
		
        String aShared = alice.getSharedKey(bPbk);
        String bShared = bob.getSharedKey(aPbk);
        
        assertEquals(aShared, bShared);
	}		
}
		
		
			
		
		
//		
//		
//		CryptoInterface a = new Crypto();
//		CryptoInterface b = new Crypto();
//		
//		String sender =(a.getPrivateKey());
//		
//		String alice = a.getPrivateKey();
//		String alicePbk = a.getPublicKey();
//		
//		String bobPbk = b.getPrivateKey();
//		String bobPrk = b.getPublicKey();
//		
//		assertSame("alice keys", alice, sender);
//		
//	}
//
//}
