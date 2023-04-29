package org.cyberark.conjur.demos.aws;

import com.amazonaws.auth.BasicSessionCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 * @author bnasslahsen
 */
@Component
public class AwsLambdaResource implements AwsResource {

	private static final String ACCESS_KEY = System.getenv("AWS_ACCESS_KEY_ID");
	private static final String SECRET_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
	private static final String TOKEN = System.getenv("AWS_SESSION_TOKEN");
	private static final String AWS_LAMBDA_ARN = System.getenv("AWS_LAMBDA_ARN");

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsEksResource.class);


	@Override
	public BasicSessionCredentials getCredentials() {
		return new BasicSessionCredentials(ACCESS_KEY, SECRET_KEY, TOKEN);
	}

	@Override
	public String getArn() {
		LOGGER.debug("AWS_LAMBDA_ARN: {}", AWS_LAMBDA_ARN);
		return AWS_LAMBDA_ARN;
	}
}
