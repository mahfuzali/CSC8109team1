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
	 * Send a message to a queue
	 * @param queueName - name of the queue
	 * @param message - a message as a serialised string
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean sendMessage(String queueName, String message, String source, String target);
	
	/**
	 * Send a document to a queue
	 * @param queueName - name of the queue
	 * @param document - a document as a string of up to 2GB
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean sendDocument(String queueName, String document, String source, String target);
	
	/**
	 * Receive a message from a queue
	 * @param queueName - name of the queue
	 * @return a message object, or null if none
	 */
	Message receiveMessage(String queueName);
	
	/**
	 * Delete a message from a queue
	 * @param queueName - name of the queue
	 * @param messageHandle - a string identifying the message
	 * @return true if successful, false otherwise
	 */
	boolean deleteMessage(String queueName, String messageHandle);
	
}
