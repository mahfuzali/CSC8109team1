package uk.ac.ncl.csc8109.team1.client;

import java.io.File;
import java.util.Map;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;;

public class Source {
	private static final String TDS_queueName = "csc8109_1_tds_queue_20070306";
	private static final String name = "alice-";
	private static String EOO;
	
	// **Starts exchange**//
	public static void main(String[] args) {
		Client alice = new Client();
		alice.setLabel("label1");
		alice.setSource("bob");
		alice.setQueueName("csc8109_1_tds_queue_20070306_alice");
		// **Request exchange from TDS, which is label(id) of exchange**//

		// **Wait for exchange id from TDS**//

		// **Send message1 to TDS
		// doc along with signature of A containing hash of doc (doc,
		// sigA(h(doc)))**//
		File f = new File("sample");
		System.out.println(sendMsg(f, alice, TDS_queueName));
		
		// **wait for message(Evidence of origin) from TDS at the of exchange
		// which is signature of B containing signature of A which contains hash
		// of the doc(sigB(sigA(h(doc)) **//

		//receiveMsg(alice, TDS_queueName);
		
		// 4. receive the eor from tds 
		
		
		// **Output then end**//
	}
	
	public static boolean sendMsg(File f, Client source, String tds_queue) {
		boolean success = false;

		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");
		
		String queue = tds_queue;
		String label = source.getLabel();
		String eoo = source.getEOO(f);
		setEOO(eoo);
		String uuid = source.getUUID();
		String target = source.getSource();

		if ((queue != null && !queue.isEmpty()) 
				&& (label != null && !label.isEmpty())
				&& (eoo != null && !eoo.isEmpty())
				&& (uuid != null && !uuid.isEmpty())
				&& (target != null && !target.isEmpty())) {
			//success = msg.sendMessage(source.getQueueName(), source.getLabel(), eoo, source.getUUID(), source.getSource());
			
			System.out.println("EOO before sending");
			System.out.println(eoo);
			success = sqsx.sendMsgDocument(queue, label, eoo, f.getPath(), name + uuid, target);
			System.out.println(eoo);
			System.out.println("EOO after sending");

			if (!success) {
				throw new IllegalArgumentException("null or empty value is passed");
			}
		}
		
		return success;
	}
	
	
	public static void receiveMsg(Client source, String tds_queue) {
		String queue = tds_queue;
		MessageInterface sqsx = new AmazonExtendedSQS("csc8109team1");

		// Receive message
        String messageHandle = null;
        Message message = sqsx.receiveMessage(queue);
        if (message != null) {
        	messageHandle = message.getReceiptHandle();
        	System.out.println("Message received from queue " + queue);
            System.out.println("  ID: " + message.getMessageId());
            System.out.println("  Receipt handle: " + messageHandle);
            System.out.println("  Message body: " + message.getBody());
            Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
            System.out.println("  Label:" + attributes.get("Label").getStringValue());
            System.out.println("  Source:" + attributes.get("Source").getStringValue());
            System.out.println("  Target:" + attributes.get("Target").getStringValue());
        }		
	}

	public String getEOO() {
		return EOO;
	}

	static void setEOO(String eOO) {
		EOO = eOO;
	}
	
	
	
}
