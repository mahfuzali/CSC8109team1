/**
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;

/**
 * @author Stephen Shephard
 *
 */
public class AmazonExtendedSQS implements MessageInterface {
	
	private final String s3BucketName;
	
	private static final Region awsRegion = Region.getRegion(Regions.EU_WEST_1);
	
	private final ProfileCredentialsProvider credentials;
	
	private final AmazonSQS sqsExtended;

	/**
	 * Constructor
	 * @param bucketName - bucket name to use for messaging
	 * @throws IllegalStateException if bucket does not exist
	 */
	public AmazonExtendedSQS(String bucketName) {
	    try {
	        credentials = new ProfileCredentialsProvider("default");
	      } catch (Exception e) {
	        throw new AmazonClientException(
	          "Cannot load the AWS credentials from the expected AWS credential profiles file. "
	          + "Make sure that your credentials file is at the correct "
	          + "location (/home/$USER/.aws/credentials) and is in a valid format.", e);
	      }

	    // Set bucket name property to passed-in value
	    this.s3BucketName = bucketName;
	    
	    // Create S3 client
	    AmazonS3 s3 = new AmazonS3Client(credentials);
	    s3.setRegion(awsRegion);
	    
	    // Check that bucket exists
	    if (!s3.doesBucketExist(s3BucketName)) {
	    	throw(new IllegalStateException("S3 bucket " + s3BucketName + " does not exist"));
	    }
	    
	    // Set the SQS extended client configuration with large payload support enabled
	    ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
	    		.withLargePayloadSupportEnabled(s3, s3BucketName);
	    
	    sqsExtended = new AmazonSQSExtendedClient(new AmazonSQSClient(credentials), extendedClientConfig);
	    sqsExtended.setRegion(awsRegion);
	}

	/**
	 * Create a message queue
	 * @param queueName - name of the queue
	 * @return true if successful, false otherwise
	 */
	public boolean create(String queueName) {
	    // Create a message queue
	    CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
	    try {
	    	sqsExtended.createQueue(createQueueRequest);
	    } catch (Exception e) {
	    	return false;
	    }
		return true;
	}

	/**
	 * Delete a message queue
	 * @param queueName - name of the queue
	 * @return true if successful, false otherwise
	 */
	public boolean delete(String queueName) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			return false;
		}
		try {
			sqsExtended.deleteQueue(new DeleteQueueRequest(queueUrl));
	    } catch (Exception e) {
	    	return false;
	    }
		return true;
	}

	/**
	 * Send a message to a queue
	 * @param queueName - name of the queue
	 * @param message - a message as a serialised string
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	public boolean sendMessage(String queueName, String message, String source, String target) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	return false;
	    }
		return true;
	}

	/**
	 * Send a document to a queue
	 * @param queueName - name of the queue
	 * @param document - a document as a string of up to 2GB
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	public boolean sendDocument(String queueName, String document, String source, String target) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Receive a message from a queue
	 * @param queueName - name of the queue
	 * @return a message object, or null if none
	 */
	public Message receiveMessage(String queueName) {
		String queueUrl;
		Message message = null;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			return null;
		}
		// Receive at most one message from the queue
		try {
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
			List<Message> messages = sqsExtended.receiveMessage(receiveMessageRequest.withMessageAttributeNames("All")).getMessages();
			// By default, getMessages() receives at most 1 message, but ensure that only one is returned
			if (messages.size() > 0) {
				message = messages.get(0);
			}			
	    } catch (Exception e) {
	    	return null;
	    }
		return message;
	}

	/**
	 * Delete a message from a queue
	 * @param queueName - name of the queue
	 * @param messageHandle - a string identifying the message
	 * @return true if successful, false otherwise
	 */
	public boolean deleteMessage(String queueName, String messageHandle) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			return false;
		}
		// Delete the message
		try {
			DeleteMessageRequest request = new DeleteMessageRequest(queueUrl, messageHandle);
			sqsExtended.deleteMessage(request);
	    } catch (Exception e) {
	    	return false;
	    }
		return true;
	}

}
