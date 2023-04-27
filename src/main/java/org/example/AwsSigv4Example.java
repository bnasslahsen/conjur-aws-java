package org.example;

import java.io.ByteArrayInputStream;
import java.net.URI;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AwsSigv4Example {
	
	private static String SERVICE_NAME = "sts";
	private static String ENDPOINT = "https://sts.amazonaws.com";
	private static String REGION = "us-east-1";

	public static String getConjurAuthnApiKey() throws JsonProcessingException {
		AWSCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(false);
		BasicSessionCredentials awsCredentials = (BasicSessionCredentials) credentialsProvider.getCredentials();
		AWS4Signer signer = new AWS4Signer();
		signer.setServiceName(SERVICE_NAME);
		signer.setRegionName(REGION);
		// Create an HTTP request
		Request request = generateRequest();
		signer.sign(request, awsCredentials);
		// Add the headers to the request
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(request.getHeaders());
	}

	/// Set up the request
	private static Request<?> generateRequest() {
		Request<?> request = new DefaultRequest<Void>(SERVICE_NAME);
		request.setContent(new ByteArrayInputStream("".getBytes()));
		request.setEndpoint(URI.create(ENDPOINT));
		request.setHttpMethod(HttpMethodName.GET);
		request.addParameter("Action", "GetCallerIdentity");
		request.addParameter("Version", "2011-06-15");
		return request;
	}
}
