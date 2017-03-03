package uk.ac.ncl.csc8109.team1.tds;

import uk.ac.ncl.csc8109.team1.msg.MessageInterface;
import uk.ac.ncl.csc8109.team1.msg.AmazonExtendedSQS;

public class tds {

	/**
	 * step 0
	 */
	public static void registerUser(){
		String Alice_id = userId();
		String Alice_key = publicKey();
		
		String Bob_id = userId();
		String Bob_key = publicKey();
	}
	
	/**
	 * step 1
	 * @param Alice_id, Alice_key, Bob_id, Bob_key
	 */
	public static void getAliceBobKey(String Alice_id, String Alice_key, String Bob_id, String Bob_key){
//		Connect to DB
//		check if Alice is existing and whether she is using the correct key
		if(Alice_id && Alice_key){
			sendMessageToDB(messageFromAlice);
		}else{
			System.out.println("Step 1(Alice) Error!");
		}
	
//		check if Bob is existing and whether he is using the correct key
		if(Bob_id && Bob_key){
			sendMessageToDB(messageFromBob);
		}else{
			System.out.println("Step 1(Bob) Error!");
		}
		
		String label = xxx;
		sendLabelToDB(label);
	}
	
	/**
	 * step 2
	 * @param label
	 */
	public static void sendLabelToAlice(String label){
//		send label to Alice
		String message_Step2 = xxx;
		sendMessageToDB(message_Step2);
	}
	
	/**
	 * step 3
	 */
	public static void receiveEOOFromAlice(doc, EOO, String label){
		String message_Step3 = xxx;
		if(doc && EOO && label){
			sendDocToS3(doc);
			sendMessageToDB(message_Step3);
		}else{
			System.out.println("Step 3 Error!");
		}
	}
	
	/**
	 * step 4
	 */
	public static void sendEOOToBob(EOO, String label){
//		send EOO and label to Bob
		String message_Step4 = xxx;
		if(EOO && label){
			sendMessageToDB(message_Step4);
		}else{
			System.out.println("Step 4 Error!");
		}
	}
	
	/**
	 * step 5
	 */
	public static void receiveEORFromBob(EOR, String label){
		String message_Step5 = xxx;
		if(EOR && label){
			sendMessageToDB(message_Step5);
		}else{
			System.out.println("Step 5 Error!");
		}
	}
	
	/**
	 * step 6
	 */
	public static void sendDocToBob(doc, String label){
//		send document to Bob
		String message_Step6 = xxx;
		if(doc && label){
			sendMessageToDB(message_Step6);
		}else{
			System.out.println("Step 6 Error!");
		}
	}
	
	/**
	 * step 7
	 * @param label
	 */
	public static void sendEORtoAlice(String label){
//		send EOR to Alice
		String message_Step7 = xxx;
		sendMessageToDB(message_Step7);
	}
	
	/**
	 * step 8
	 * @param Alice_label
	 * @param Bob_label
	 */
	public static void receiveBothLabel(String Alice_label, String Bob_label){
		if(Alice_label && Bob_label){
			System.out.println("Fair Exchange Finished!");
		}else{
			System.out.println("Step 8 Error!");
		}
	}
	
	
}
