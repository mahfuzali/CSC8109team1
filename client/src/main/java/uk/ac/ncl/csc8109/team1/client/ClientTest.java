package uk.ac.ncl.csc8109.team1.client;

public class ClientTest {
	public static void main(String[] args) {
		Client c = new Client();
		c.setLabel("label2");
		c.setTds("tds2");
		c.setSource("source2");
		
		System.out.println(c.getUUID());
		System.out.println(c.getPublicKey());
		System.out.println(c.getPrivateKey());
		System.out.println(c.getQueueName());
		
		System.out.println(c.getLabel());
		System.out.println(c.getTds());
		System.out.println(c.getSource());
		
		System.out.println(c.getTds() + "," +  c.getSource() + "," + c.getLabel());
	
		c.writeToFile("teamPath", c.getTds(), c.getSource(), c.getLabel());
		c.readFromFile("teamPath");
	}

}
