/**
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
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
public class AmazonExtendedSQS implements MessageService {
	
	private static final String s3BucketName = UUID.randomUUID() + "-"
		    + DateTimeFormat.forPattern("yyMMdd-hhmmss").print(new DateTime());
	
	private static final Region awsRegion = Region.getRegion(Regions.EU_WEST_1);
	
	private AWSCredentials credentials = null;
	
	private AmazonSQS sqsExtended = null;

	/**
	 * 
	 */
	public AmazonExtendedSQS() {
	    try {
	        credentials = new ProfileCredentialsProvider("default").getCredentials();
	      } catch (Exception e) {
	        throw new AmazonClientException(
	          "Cannot load the AWS credentials from the expected AWS credential profiles file. "
	          + "Make sure that your credentials file is at the correct "
	          + "location (/home/$USER/.aws/credentials) and is in a valid format.", e);
	      }
	   
	      AmazonS3 s3 = new AmazonS3Client(credentials);
	      s3.setRegion(awsRegion);
	   
	      // Set the Amazon S3 bucket name, and set a lifecycle rule on the bucket to
	      //   permanently delete objects a certain number of days after
	      //   each object's creation date.
	      //   Then create the bucket, and enable message objects to be stored in the bucket.
	      BucketLifecycleConfiguration.Rule expirationRule = new BucketLifecycleConfiguration.Rule();
	      expirationRule.withExpirationInDays(14).withStatus("Enabled");
	      BucketLifecycleConfiguration lifecycleConfig = new BucketLifecycleConfiguration().withRules(expirationRule);
	   
	      s3.createBucket(s3BucketName);
	      s3.setBucketLifecycleConfiguration(s3BucketName, lifecycleConfig);
	      
	      // Set the SQS extended client configuration with large payload support enabled.
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
		String queue_url;
		// Get the Queue URL
		try {
			queue_url = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			return false;
		}
		try {
			sqsExtended.deleteQueue(new DeleteQueueRequest(queue_url));
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
		String queue_url;
		// Get the Queue URL
		try {
			queue_url = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queue_url);
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

	/* (non-Javadoc)
	 * @see uk.ac.ncl.csc8109.team1.msg.MessageService#receiveMessage(java.lang.String)
	 */
	public String receiveMessage(String queueName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.csc8109.team1.msg.MessageService#deleteMessage(java.lang.String, java.lang.String)
	 */
	public boolean deleteMessage(String queueName, String messageHandle) {
		// TODO Auto-generated method stub
		return false;
	}

}
