/**
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
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
	
	// private static final String s3BucketName = UUID.randomUUID() + "-" + DateTimeFormat.forPattern("yyMMdd-hhmmss").print(new DateTime());
	private static final String s3BucketName = "csc8109team1";
	
	private static final Regions awsRegion = Regions.EU_WEST_1;
	
	private ProfileCredentialsProvider credentials = null;
	
	private AmazonSQS sqsExtended = null;

	/**
	 * Constructor
	 */
	public AmazonExtendedSQS() {
	    try {
	        credentials = new ProfileCredentialsProvider("default");
	      } catch (Exception e) {
	        throw new AmazonClientException(
	          "Cannot load the AWS credentials from the expected AWS credential profiles file. "
	          + "Make sure that your credentials file is at the correct "
	          + "location (/home/$USER/.aws/credentials) and is in a valid format.", e);
	      }

	    AmazonS3 s3 = AmazonS3ClientBuilder.standard()
	    		.withRegion(awsRegion)
                .withCredentials(credentials)
                .build();

	    // Code to create a bucket.. code to check for a bucket and create if not found?
	    
	    // Set the Amazon S3 bucket name, and set a lifecycle rule on the bucket to
	    //   permanently delete objects a certain number of days after
	    //   each object's creation date.
	    //   Then create the bucket, and enable message objects to be stored in the bucket.
	    
	    // BucketLifecycleConfiguration.Rule expirationRule = new BucketLifecycleConfiguration.Rule();
	    // expirationRule.withExpirationInDays(14).withStatus("Enabled");
	    // BucketLifecycleConfiguration lifecycleConfig = new BucketLifecycleConfiguration().withRules(expirationRule);

	    // s3.createBucket(s3BucketName);
	    // s3.setBucketLifecycleConfiguration(s3BucketName, lifecycleConfig);

	    // Set the SQS extended client configuration with large payload support enabled
	    ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
	    		.withLargePayloadSupportEnabled(s3, s3BucketName);

	    AmazonSQS sqs = AmazonSQSClientBuilder.standard()
	    		.withRegion(awsRegion)
                .withCredentials(credentials)
                .build();
	    
	    sqsExtended = new AmazonSQSExtendedClient(sqs, extendedClientConfig);
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
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	public boolean sendMessage(String queueName, String message, String target) {
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

	/* (non-Javadoc)
	 * @see uk.ac.ncl.csc8109.team1.msg.MessageService#sendDocument(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean sendDocument(String queueName, String document, String target) {
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
