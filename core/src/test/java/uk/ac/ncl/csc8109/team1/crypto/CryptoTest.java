package uk.ac.ncl.csc8109.team1.crypto;


import uk.ac.ncl.csc8109.team1.crypto.CryptoInterface;
import uk.ac.ncl.csc8109.team1.crypto.CryptoApp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;




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
	private String bhashDiffFile;
	
	
	CryptoApp a = new CryptoApp();
	
	CryptoInterface alice = new Crypto();
	CryptoInterface bob = new Crypto();
	File f = new File("sample");
	File f1 = new File("Untitled 2");
	private String aliceCheck;
	private String bobCheck;
	String techPath = "tech";
	Path tech = Paths.get(techPath);
	String techPath2 = "tech2";
	Path tech2 = Paths.get(techPath2);
	
	private File bobDecryptFile;
	private File aliceEncryptFile;
	
	
	
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
	    
	    aShared = alice.getSharedKey(bPbk);
	    bShared = bob.getSharedKey(aPbk);

	}
	
	@Test
	public void testSharedKey() {  
        assertEquals(aShared, bShared);
	}
	
	@Test
	public void testHash(){
		assertEquals(ahash, bhash);
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
	public void testBobPublicKeys(){
		 assertEquals(bPbk, bPbk);
	}
	
	@Test
	public void testPublicKeyString(){
		assertTrue(aPbk instanceof String);
	}
	
	@Test
	public void testPrivateKeyString(){
		assertTrue(aPrk instanceof String);
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
	public void testSignatureFlase(){
		assertFalse(asignature.equals(bsignature));
	}



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

	public String getAliceCheck() {
		return aliceCheck;
	}

	public void setAliceCheck(String aliceCheck) {
		this.aliceCheck = aliceCheck;
	}

	public String getBobCheck() {
		return bobCheck;
	}

	public void setBobCheck(String bobCheck) {
		this.bobCheck = bobCheck;
	}

	public File getBobDecryptFile() {
		return bobDecryptFile;
	}

	public void setBobDecryptFile(File bobDecryptFile) {
		this.bobDecryptFile = bobDecryptFile;
	}

	public File getAliceEncryptFile() {
		return aliceEncryptFile;
	}

	public void setAliceEncryptFile(File aliceEncryptFile) {
		this.aliceEncryptFile = aliceEncryptFile;
	}
}
