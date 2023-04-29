package org.cyberark.conjur.demos.aws;

import java.util.Objects;

import com.amazonaws.auth.BasicSessionCredentials;

import org.springframework.stereotype.Component;

import static org.cyberark.conjur.demos.aws.Constants.AWS_SERVICE_TYPE;

/**
 * @author bnasslahsen
 */
@Component
public final class AwsResourceProvider implements AwsResource {
	
	private final AwsResource awsResource;

	public AwsResourceProvider(AwsEc2Resource awsEc2Resource, AwsEksResource awsEksResource, AwsLambdaResource awsLambdaResource) {
		Objects.requireNonNull(AWS_SERVICE_TYPE, "AWS Service Type is null. Define environment variable AWS_SERVICE_TYPE");
		AwsServiceType awsServiceType = AwsServiceType.valueOf(AWS_SERVICE_TYPE);
		switch (awsServiceType) {
			case EC2:
				awsResource = awsEc2Resource;
				break;
			case EKS:
				awsResource = awsEksResource;
				break;
			case LAMBDA:
				awsResource = awsLambdaResource;
				break;
			default:
				throw new IllegalArgumentException("Unsupported AWS Service Type: " + awsServiceType);
		}
	}

	@Override
	public BasicSessionCredentials getCredentials() {
		return awsResource.getCredentials();
	}

	@Override
	public String getArn() {
		return awsResource.getArn();
	}
}
