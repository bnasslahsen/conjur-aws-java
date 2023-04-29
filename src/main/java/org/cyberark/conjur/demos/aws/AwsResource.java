package org.cyberark.conjur.demos.aws;

import com.amazonaws.auth.BasicSessionCredentials;

/**
 * @author bnasslahsen
 */
public interface AwsResource {

	BasicSessionCredentials getCredentials();

	String getArn();
}
