package uk.ac.ncl.csc8109.team1.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TargetTest extends TestCase {
    
	public TargetTest( String testName )
    {

    }
	
    public static Test suite()
    {
        return new TestSuite( SourceTest.class );
    }
}
