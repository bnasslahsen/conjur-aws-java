package org.cyberark.conjur.demos.aws;

/**
 * @author bnasslahsen
 */
public class Constants {

	static final String CONJUR_SSL_CERTIFICATE = 	System.getenv().getOrDefault("CONJUR_SSL_CERTIFICATE", null);
	static final String CONJUR_CERT_FILE =System.getenv().getOrDefault("CONJUR_CERT_FILE", null);
	static final String CONJUR_PREFIX= System.getenv().getOrDefault("CONJUR_PREFIX", "host/data/bnl/aws-apps" );
	static final String CONJUR_SECRET_KEY =  System.getenv().getOrDefault("CONJUR_PREFIX", "jwt-apps/secrets/password" );

	static final String AWS_SERVICE_TYPE = System.getenv().getOrDefault("AWS_SERVICE_TYPE", null);;
	static final String AWS_SERVICE_NAME =  System.getenv().getOrDefault("SERVICE_NAME", "sts" ) ;
	static final String AWS_ENDPOINT = System.getenv().getOrDefault("ENDPOINT", "https://sts.amazonaws.com" ) ;
	static final String AWS_REGION = System.getenv().getOrDefault("REGION", "us-east-1" );
	static final String AWS_WEB_IDENTITY_TOKEN_FILE = System.getenv().getOrDefault("WEB_IDENTITY_TOKEN_FILE", "/var/run/secrets/eks.amazonaws.com/serviceaccount/token" );

	static final String REQUEST_ACTION = "Action";
	static final String REQUEST_VERSION = "Version";
	static final String GET_CALLER_IDENTITY = "GetCallerIdentity";
	static final String GET_CALLER_IDENTITY_VERSION = "2011-06-15";
	static final String CONJUR_TLS_CA_PATH = "conjurTlsCaPath";
	public static final String X_509 = "X.509";
}
