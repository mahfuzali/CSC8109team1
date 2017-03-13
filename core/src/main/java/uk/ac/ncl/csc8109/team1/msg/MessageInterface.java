/**
 * Message Service interface
 * 
 * Provides a level of abstraction to Amazon SQS messaging
 * Assumes SQS - and uses its message type
 * but can use other implementations e.g. Extended SQS
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import com.amazonaws.services.sqs.model.Message;

/**
 * @author Stephen Shephard
 *
 */
public interface MessageInterface {
	
	/**
	 * Create a message queue
	 * @param queueName - name of the queue
	 * @return true if successful, false otherwise
	 */
	boolean create(String queueName);

	/**
	 * Delete a message queue
	 * @param queueName - name of the queue
	 * @return true if successful, false otherwise
	 */
	boolean delete(String queueName);
	
	/**
	 * Send a user registration request to the TDS queue
	 * @param queueName - name of the queue
	 * @param userid - id of the user to register
	 * @param publicKey - user's public key
	 * @return true if successful, false otherwise
	 */
	boolean registerRequest(String queueName, String userid, String publicKey);
	
	/**
	 * Send a user registration request response to the TDS queue
	 * @param tdsQueueName - name of the TDS registration queue
	 * @param userid - id of the user to register
	 * @param targetQueueName - name of the target user's newly created queue
	 * @return true if successful, false otherwise
	 */
	boolean registerResponse(String tdsQueueName, String userid, String targetQueueName);
	
	/**
	 * Send an exchange request to the TDS queue (client request to TDS for an exchange label)
	 * @param queueName - name of the queue
	 * @param protocol - exchange protocol name
	 * @param message - a message SigA("ExchangeRequest")
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean exchangeRequest(String queueName, String protocol, String message, String source, String target);
	
	/**
	 * Send a message to a queue
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message as a serialised string
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean sendMessage(String queueName, String label, String message, String source, String target);
	
	/**
	 * Send an abort exchange request to the TDS queue
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message SigA("AbortRequest")
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean abortRequest(String queueName, String label, String message, String source, String target);
	
	/**
	 * Send a message and attached document to a queue
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message as a serialised string
	 * @param document - a filename of a document
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean sendMsgDocument(String queueName, String label, String message, String document, String source, String target);
	
	/**
	 * Receive a message from a queue
	 * @param queueName - name of the queue
	 * @return a message object, or null if none
	 */
	Message receiveMessage(String queueName);
	
	/**
	 * Receive a message for a specific user from a queue accessed by many users
	 * @param queueName - name of the queue
	 * @param userid - id of the user
	 * @return a message object, or null if none
	 */
	Message receiveMyMessage(String queueName, String userid);
	
	/**
	 * Delete a message from a queue
	 * @param queueName - name of the queue
	 * @param messageHandle - a string identifying the message
	 * @return true if successful, false otherwise
	 */
	boolean deleteMessage(String queueName, String messageHandle);
	
}
