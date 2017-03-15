/**
 * 
 */
package uk.ac.ncl.csc8109.team1.msg;

import java.util.Map;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	    	e.printStackTrace();
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
			e.printStackTrace();
			return false;
		}
		try {
			sqsExtended.deleteQueue(new DeleteQueueRequest(queueUrl));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
	/**
	 * Send an exchange request to the TDS queue (client request to TDS for an exchange label)
	 * @param queueName - name of the queue
	 * @param protocol - exchange protocol name
	 * @param message - a message SigA("ExchangeRequest")
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	public boolean exchangeRequest(String queueName, String protocol, String message, String source, String target) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Protocol", new MessageAttributeValue().withDataType("String.Protocol").withStringValue(protocol));
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}

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
	public boolean exchangeResponse(String queueName, String label, String message, String source, String target, String targetkey) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Label", new MessageAttributeValue().withDataType("String.Label").withStringValue(label));
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
			messageAttributes.put("TargetKey", new MessageAttributeValue().withDataType("String.TargetKey").withStringValue(targetkey));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
	/**
	 * Send a message to a queue
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message as a serialised string
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	public boolean sendMessage(String queueName, String label, String message, String source, String target) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Label", new MessageAttributeValue().withDataType("String.Label").withStringValue(label));
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
	/**
	 * Send a message and source's public key to a queue
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message as a serialised string
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @param sourcekey - the source's public key
	 * @return true if successful, false otherwise
	 */
	public boolean sendMsgSourceKey(String queueName, String label, String message, String source, String target, String sourcekey) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Label", new MessageAttributeValue().withDataType("String.Label").withStringValue(label));
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
			messageAttributes.put("SourceKey", new MessageAttributeValue().withDataType("String.SourceKey").withStringValue(sourcekey));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
	/**
	 * Send an abort exchange request to the TDS queue
	 * @param queueName - name of the queue
	 * @param label - exchange label
	 * @param message - a message SigA("AbortRequest")
	 * @param source - the userid of the original source of the message
	 * @param target - the userid of the ultimate recipient of the message
	 * @return true if successful, false otherwise
	 */
	public boolean abortRequest(String queueName, String label, String message, String source, String target) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Abort", new MessageAttributeValue().withDataType("String.Abort").withStringValue("AbortRequest"));
			messageAttributes.put("Label", new MessageAttributeValue().withDataType("String.Label").withStringValue(label));
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
	/**
	 * Send a user registration request to the TDS queue
	 * @param queueName - name of the queue
	 * @param userid - id of the user to register
	 * @param publicKey - user's public key
	 * @return true if successful, false otherwise
	 */
	public boolean registerRequest(String queueName, String userid, String publicKey) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Userid", new MessageAttributeValue().withDataType("String.Userid").withStringValue(userid));
			messageAttributes.put("PublicKey", new MessageAttributeValue().withDataType("String.PublicKey").withStringValue(userid));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody("Registration Request");
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
	/**
	 * Send a user registration request response to the TDS queue
	 * @param tdsQueueName - name of the TDS registration queue
	 * @param userid - id of the user to register
	 * @param targetQueueName - name of the target user's newly created queue
	 * @return true if successful, false otherwise
	 */
	public boolean registerResponse(String tdsQueueName, String userid, String targetQueueName) {
		String queueUrl;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(tdsQueueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(userid));
			messageAttributes.put("Queue", new MessageAttributeValue().withDataType("String.Queue").withStringValue(targetQueueName));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody("Registration Response");
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
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
	public boolean sendMsgDocument(String queueName, String label, String message, String document, String source, String target) {
		String queueUrl;
		Path docPath;
		byte[] docByteArray;
		ByteBuffer docByteBuffer;
		
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// Read the document file to a byte buffer
		try {
			docPath = Paths.get(document);
			docByteArray = Files.readAllBytes(docPath);
			docByteBuffer = ByteBuffer.wrap(docByteArray);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// Build and send the message
		try {
			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Label", new MessageAttributeValue().withDataType("String.Label").withStringValue(label));
			messageAttributes.put("Source", new MessageAttributeValue().withDataType("String.Source").withStringValue(source));
			messageAttributes.put("Target", new MessageAttributeValue().withDataType("String.Target").withStringValue(target));
			messageAttributes.put("DocumentName", new MessageAttributeValue().withDataType("String.DocumentName").withStringValue(document));
			messageAttributes.put("Document", new MessageAttributeValue().withDataType("Binary.Document").withBinaryValue(docByteBuffer));
		    SendMessageRequest request = new SendMessageRequest();
		    request.withMessageBody(message);
		    request.withQueueUrl(queueUrl);
		    request.withMessageAttributes(messageAttributes);
		    sqsExtended.sendMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
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
			e.printStackTrace();
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
	    	e.printStackTrace();
	    	return null;
	    }
		return message;
	}
	
	/**
	 * Receive a message for a specific user from a queue accessed by many users
	 * @param queueName - name of the queue
	 * @param userid - id of the user
	 * @return a message object, or null if none
	 */
	public Message receiveMyMessage(String queueName, String userid) {
		String queueUrl;
		Message message = null;
		String messageHandle = null;
		// Get the Queue URL
		try {
			queueUrl = sqsExtended.getQueueUrl(queueName).getQueueUrl();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// Receive at most 10 messages from the queue
		try {
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
			List<Message> messages = sqsExtended.receiveMessage(receiveMessageRequest.withMessageAttributeNames("All").withMaxNumberOfMessages(10)).getMessages();
			// Check all the messages received
			Map<String, MessageAttributeValue> attributes;
			MessageAttributeValue attr;
			for (Message m: messages) {
				// Check the attributes of the message
	            attributes = m.getMessageAttributes();
	            attr = attributes.get("Target");
	            // If the message has a Target attribute, and it matches the userid, then return this message
	            if (attr != null && attr.getStringValue().equals(userid)) {
	            	message = m;
	            	break;
	            } else {
	            	// Make this message immediately visible to others
	            	messageHandle = m.getReceiptHandle();
	            	sqsExtended.changeMessageVisibility(queueUrl, messageHandle, 0);
	            }
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
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
			e.printStackTrace();
			return false;
		}
		// Delete the message
		try {
			DeleteMessageRequest request = new DeleteMessageRequest(queueUrl, messageHandle);
			sqsExtended.deleteMessage(request);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}

}
