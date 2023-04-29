package org.cyberark.conjur.demos.aws;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import static org.cyberark.conjur.demos.aws.Constants.AWS_WEB_IDENTITY_TOKEN_FILE;

/**
 * @author bnasslahsen
 */
@Component
public class AwsEksResource implements AwsResource {

	private static final String AWS_ROLE_ARN = System.getenv().getOrDefault("AWS_ROLE_ARN", null);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AwsEksResource.class);

	@Override
	public BasicSessionCredentials getCredentials() {
		WebIdentityTokenCredentialsProvider credentialsProvider = WebIdentityTokenCredentialsProvider.builder()
				.webIdentityTokenFile(AWS_WEB_IDENTITY_TOKEN_FILE)
				.build();
		return (BasicSessionCredentials) credentialsProvider.getCredentials();

	}

	@Override
	public String getArn() {
		LOGGER.debug("AWS_ROLE_ARN: {}", AWS_ROLE_ARN);
		return AWS_ROLE_ARN;
	}

}
