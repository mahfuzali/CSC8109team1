Message Service API
===================

An implentation of the Message Service interface will be created for Amazon SQS.  That is, when an instance of AmazonSQS is created, it can be used to create and work with Queues in that service.

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
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	boolean sendMessage(String queueName, String message, String target);
	
	/**
	 * Receive a message from a queue
	 * @param queueName - name of the queue
	 * @return a message as a serialised string, or null if none
	 */
	String receiveMessage(String queueName);
	
	/**
	 * Delete a message from a queue
	 * @param queueName - name of the queue
	 * @param messageHandle - a string identifying the message
	 * @return true if successful, false otherwise
	 */
	boolean deleteMessage(String queueName, String messageHandle);
