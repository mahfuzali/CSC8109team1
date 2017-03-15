# Resources

Credentials file for Amazon services
====================================

This directory contains a `credentials` file for the Amazon user `tdsuser`.  Your code can use this to log into Amazon Web Services and access resources that `tdsuser` has access to.

Create a directory `.aws` in your user home directory on the computer you run your code on, and copy `credentials` to it.

To load the credentials from the file, use the code below:

	AWSCredentials credentials;
	
	try {
		credentials = new ProfileCredentialsProvider("default").getCredentials();
	  } catch (Exception e) {
		throw new AmazonClientException(
		  "Cannot load the AWS credentials from the expected AWS credential profiles file. "
		  + "Make sure that your credentials file is at the correct "
		  + "location (/home/$USER/.aws/credentials) and is in a valid format.", e);
	  }

You can then use the credentials e.g.

	AmazonS3 s3 = new AmazonS3Client(credentials);