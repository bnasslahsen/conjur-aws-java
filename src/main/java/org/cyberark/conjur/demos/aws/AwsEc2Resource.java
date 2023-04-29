package org.cyberark.conjur.demos.aws;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.util.EC2MetadataUtils;

import org.springframework.stereotype.Component;

/**
 * @author bnasslahsen
 */
@Component
public class AwsEc2Resource implements AwsResource {

	@Override
	public BasicSessionCredentials getCredentials() {
		return (BasicSessionCredentials) InstanceProfileCredentialsProvider.getInstance().getCredentials();
	}

	@Override
	public String getArn() {
		return EC2MetadataUtils.getIAMInstanceProfileInfo().instanceProfileArn;
	}
}
