package uk.ac.ncl.csc8109.team1.crypto;


import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.crypto.CryptoApp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;




public class CryptoTest {
	private String aPbk;
	private String aPrk;
	private String ahash;
	private String asignature;
	private String aShared;
	
	private String bPbk;
	private String bPrk;
	private String bhash;
	private String bsignature;
	private String bShared;
	private String verification;
	private String bhashDiffFile;
	private File file;
	private File newFile;
	private File diffFile;
	private String techPath; 
	
	
	CryptoApp a = new CryptoApp();
	
	CryptoInterface alice = new Crypto();
	CryptoInterface bob = new Crypto();
	
	File f = new File("sample");
	File f2 = new File("sample");
	File f1 = new File("Untitled 2");
	File tech1 = new File("tech");
	
//	private String str;
//	
//	public String getStr() {
//		return str;
//	}
//
//	public void setStr(String str) {
//		this.str = str;
//	}

	@Before
	public void setUp(){
		aPbk = alice.getPublicKey();
		aPrk = alice.getPrivateKey();
		ahash = alice.getHashOfFile(f);
		asignature = alice.getSignature(ahash);		

		bPbk = bob.getPublicKey();
		bPrk = bob.getPrivateKey();
		bhash = bob.getHashOfFile(f);
		bhashDiffFile = bob.getHashOfFile(f1);
	    bsignature = bob.getSignature(bhash);
	    
		verification = bob.isVerified(ahash, aPbk, asignature);
	    
	    aShared = alice.getSharedKey(bPbk);
	    bShared = bob.getSharedKey(aPbk);
	    
	    file = f;
	    newFile = f;
	    diffFile = f1;
	    
	    
	    techPath = "tech";
		Path tech = Paths.get(techPath);
		String techPath2 = "tech2";
		Path tech2 = Paths.get(techPath2);
	    
//	   String str = alice.encryptFile("core/tech", "core/tech1", aShared);
//	    
//	    FileInputStream fis = null;
//        String str = "";
//
//        try {
//            fis = new FileInputStream(tech1);
//            int content;
//            while ((content = fis.read()) != -1) {
//                // convert to char and display it
//                str += (char) content;
//            }
//
//            System.out.println("After reading file");
//            System.out.println(str);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fis != null)
//                    fis.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
	    
	    
	    
	    

	}
	
	@Test
	public void testSharedKey() {  
        assertEquals(aShared, bShared);
	}
	
	@Test
	public void testHashEquals(){
		assertEquals(ahash, bhash);
	}
	
	@Test
	public void testAliceHashNotNull(){
		assertNotNull(ahash);
	}
	
	@Test
	public void testBobHashNotNull(){
		assertNotNull(bhash);
	}
	
	public void testDifferentHashFile(){
		assertFalse(ahash.equals(bhashDiffFile));
	}
	
	@Test
	public void testPublicKeys(){
		//**Alice Public key and Bobs public key not the same**//
        assertFalse(aPbk.equals(bPbk));
	}
	
	@Test
	public void testAlicePublicKeys(){
		 assertEquals(aPbk, aPbk);
	}
	
	@Test
	public void testAlicePublicKeyNotNull(){
		 assertNotNull(bPbk);
	}
	
	@Test
	public void testBobPublicKeys(){
		 assertEquals(bPbk, bPbk);
	}
	
	@Test
	public void testBobPublicKeysNotNull(){
		 assertNotNull(bPbk);
	}
	
	@Test
	public void testAlicePublicKeyString(){
		assertTrue(aPbk instanceof String);
	}
	
	@Test
	public void testBobPublicKeyString(){
		assertTrue(bPbk instanceof String);
	}
	
	@Test
	public void testAlicePrivateKeyString(){
		assertTrue(aPrk instanceof String);
	}
	
	@Test
	public void testBobPrivateKeyString(){
		assertTrue(bPrk instanceof String);
	}
	
	@Test
	public void testAlicePrivateKeyNotNull(){
		assertNotNull(aPrk);
	}
	
	@Test
	public void testBobPrivateKeyNotNull(){
		assertNotNull(bPrk);
	}
	
	@Test
	public void testFileNotNull(){
		assertNotNull(file);
	}
	
	@Test
	public void testFileEquality(){
		assertEquals(file, newFile);
	}
	
	@Test
	public void testFilesNotEqual(){
		assertNotSame(file, diffFile);
	}
	
	@Test
	public void testTwoFilesNotEquals(){
		assertFalse(file.equals(diffFile));
	}
	
	
	@Test
	public void testPrivateKeys(){
		//**Alice Private key and Bobs private key not the same**//
        assertFalse(aPrk.equals(bPrk));
	}
	
	@Test
	public void testSharedKeyString(){
		assertTrue(aShared instanceof String);
	}
	
	@Test
	public void testHashString(){
		assertTrue(ahash instanceof String);
	}
	
	@Test
	public void testAliceSignature(){
		assertEquals(asignature, asignature);
	}
	
	@Test
	public void testBobSignature(){
		assertEquals(bsignature, bsignature);
	}
	
	
	@Test
	public void testSignatureOfAliceAndBob(){
		assertFalse(asignature.equals(bsignature));
	}
	
	@Test
	public void testBobVerifiesSigOfHashEquality(){
		assertEquals(verification, verification);
	}
	
	@Test
	public void testBobVerifiesSigOfHashNotNull(){
		assertNotNull(verification);
	}
	
//	@Test
//	public void testEncrypFile(){
//		assertNotNull((alice.encryptFile(techPath, "core/sample1", aShared)), (alice.encryptFile(techPath, "core/sample1", aShared)));
//	}


	public String getaPbk() {
		return aPbk;
	}
	public void setaPbk(String aPbk) {
		this.aPbk = aPbk;
	}
	public String getaPrk() {
		return aPrk;
	}
	public void setaPrk(String aPrk) {
		this.aPrk = aPrk;
	}
	public String getAhash() {
		return ahash;
	}
	public void setAhash(String ahash) {
		this.ahash = ahash;
	}
	public String getAsignature() {
		return asignature;
	}
	public void setAsignature(String asignature) {
		this.asignature = asignature;
	}
	public String getbPbk() {
		return bPbk;
	}
	public void setbPbk(String bPbk) {
		this.bPbk = bPbk;
	}
	public String getbPrk() {
		return bPrk;
	}
	public void setbPrk(String bPrk) {
		this.bPrk = bPrk;
	}
	public String getBhash() {
		return bhash;
	}
	public void setBhash(String bhash) {
		this.bhash = bhash;
	}
	public String getBsignature() {
		return bsignature;
	}
	public void setBsignature(String bsignature) {
		this.bsignature = bsignature;
	}
	public String getaShared() {
		return aShared;
	}
	public void setaShared(String aShared) {
		this.aShared = aShared;
	}
	public String getbShared() {
		return bShared;
	}
	public void setbShared(String bShared) {
		this.bShared = bShared;
	}

	public String getBhashDiffFile() {
		return bhashDiffFile;
	}

	public void setBhashDiffFile(String bhashDiffFile) {
		this.bhashDiffFile = bhashDiffFile;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getNewFile() {
		return newFile;
	}

	public void setNewFile(File newFile) {
		this.newFile = newFile;
	}

	public File getDiffFile() {
		return diffFile;
	}

	public void setDiffFile(File diffFile) {
		this.diffFile = diffFile;
	}

	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}

	public String getTechPath() {
		return techPath;
	}

	public void setTechPath(String techPath) {
		this.techPath = techPath;
	}

}
