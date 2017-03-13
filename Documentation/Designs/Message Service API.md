Message Service API
===================

An implentation of the Message Service interface has beem created for Amazon SQS.  That is, when an instance of AmazonSQS is created, it can be used to create and work with Queues in that service.

Call the constructor to set up a connection to the messaging service.  See MessageApp.java for example usage.

A new client must first register by sending a registration request to the TDS registration queue

* Client calls `registerRequest`
* TDS receives the request message, calls `registerResponse` to send back a new queue name for the client, then deletes the request message from the queue  using `deleteMessage`
* Client receives the request response on the TDS registration queue using `receiveMyMessage`, then deletes it from the queue using `deleteMessage`

A registered client may then request a new exchange to the normal TDS message queue

* Client calls `exchangeRequest`
* TDS receives the request message, calls `exchangeResponse` to send back a label (exchange ID) and the public queue of the target (recipient), then deletes the request message from the queue using `deleteMessage`
* Client receives the request response on its own queue using `receiveMessage`, then deletes it from the queue using `deleteMessage`

The normal message sequence is:

* Client sends a message with `sendMessage` or `sendMsgDocument` to the TDS message queue
* TDS receives the message with `receiveMessage`, process it and then deletes the request message from the queue using `deleteMessage`
* TDS sends messages to the source or target clients using `sendMessage` to the client queue
* Clients receive messages on their own queue using `receiveMessage`, then delete them from the queue using `deleteMessage`

A client may attempt to abort an exchange by sending `abortRequest` to the TDS message queue


	/**
	 * Constructor
	 * @param bucketName - bucket name to use for messaging
	 * @throws IllegalStateException if bucket does not exist
	 */
	AmazonExtendedSQS(String bucketName);

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
	 * Send a response to an exchange request - will return the target public key in a targetkey attribute
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message "ExchangeResponse"
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @param targetkey - the public key of the target
	 * @return true if successful, false otherwise
	 */
	boolean exchangeResponse(String queueName, String label, String message, String source, String target, String targetkey);
	
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
