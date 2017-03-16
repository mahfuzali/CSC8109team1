package uk.ac.ncl.csc8109.team1.client;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ClientTest extends TestCase{
    
	Client alice;
	File f;
	String eoo;
	String eor;
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ClientTest( String testName )
    {
        super( testName );
        alice = new Client("Alice");
        alice.setLabel("label");
        alice.setDestination("Bob"); 
        alice.setQueueName("queue");
		System.out.println(alice.getUUID());
		System.out.println(alice.getPublicKey());
		System.out.println(alice.getPrivateKey());
		f = new File("teampath");

		eoo = alice.generateEOO(f);
		eor = alice.generateEOR(eoo);
    }

    
    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ClientTest.class );
    }

    
    public void testDestination()
    {
        assertEquals("Bob", alice.getDestination());
    }

    public void testQueueName()
    {
        assertEquals("queue", alice.getQueueName());
    }
    
    public void testUUID()
    {
        assertTrue(alice.getUUID() instanceof String);
    }
    
    public void testPublicKey()
    {
        assertTrue(alice.getPublicKey() instanceof String);
    }
    
    public void testPrivateKey()
    {
        assertTrue(alice.getPrivateKey() instanceof String);
    }
    
    public void testEOOType() {
		assertTrue(eoo instanceof String);
    }
    
    public void testEOO() {
		assertNotSame(eoo, 1234);
    }
 
    public void testEORType() {
		assertTrue(eor instanceof String);
    }
    
    public void testEOR() {
		System.out.println(eor);
		assertNotSame(eoo, "abcdef");
    }
    
    public void testEOOandEOR() {
		assertNotSame(eoo, eor);
    }   
    
    
}
