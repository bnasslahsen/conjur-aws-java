package org.cyberark.conjur.demos.aws.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.arn.Arn;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberark.conjur.demos.aws.common.AwsResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.TLS;
import static org.cyberark.conjur.demos.aws.common.Constants.AWS_ENDPOINT;
import static org.cyberark.conjur.demos.aws.common.Constants.AWS_REGION;
import static org.cyberark.conjur.demos.aws.common.Constants.AWS_SERVICE_NAME;
import static org.cyberark.conjur.demos.aws.common.Constants.CONJUR_CERT_FILE;
import static org.cyberark.conjur.demos.aws.common.Constants.CONJUR_PREFIX;
import static org.cyberark.conjur.demos.aws.common.Constants.CONJUR_SSL_CERTIFICATE;
import static org.cyberark.conjur.demos.aws.common.Constants.CONJUR_TLS_CA_PATH;
import static org.cyberark.conjur.demos.aws.common.Constants.GET_CALLER_IDENTITY;
import static org.cyberark.conjur.demos.aws.common.Constants.GET_CALLER_IDENTITY_VERSION;
import static org.cyberark.conjur.demos.aws.common.Constants.REQUEST_ACTION;
import static org.cyberark.conjur.demos.aws.common.Constants.REQUEST_VERSION;
import static org.cyberark.conjur.demos.aws.common.Constants.X_509;

/**
 * @author bnasslahsen
 */
@Component
public class ConjurAwsService {

	private final AwsResourceProvider awsResourceProvider;
	
	private final ObjectMapper objectMapper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConjurAwsService.class);

	public ConjurAwsService(AwsResourceProvider awsResourceProvider, ObjectMapper objectMapper) {
		this.awsResourceProvider = awsResourceProvider;
		this.objectMapper = objectMapper;
	}
	
	public String getConjurAuthnLogin() {
		String arnString = awsResourceProvider.getArn();
		Arn arn = Arn.fromString(arnString);
		String roleName = arn.getResourceAsString().split("/")[1];
		String conjurAuthnLogin = String.format(CONJUR_PREFIX + "/%s/%s", arn.getAccountId(), roleName);
		LOGGER.debug("Conjur authn login: {}", conjurAuthnLogin);
		return conjurAuthnLogin;
	}

	public String getConjurAuthnApiKey() throws JsonProcessingException {
		BasicSessionCredentials awsCredentials = awsResourceProvider.getCredentials();
		AWS4Signer signer = new AWS4Signer();
		signer.setServiceName(AWS_SERVICE_NAME);
		signer.setRegionName(AWS_REGION);
		// Create an HTTP request
		Request request = generateRequest();
		// Sign it!
		signer.sign(request, awsCredentials);
		String signedRequestHeaders = objectMapper.writeValueAsString(request.getHeaders());
		LOGGER.debug("Signed request headers: {}", signedRequestHeaders);
		return signedRequestHeaders;
	}

	public SSLContext createSslContext() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		final Certificate certificate = generateCertificate();
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null);
		keyStore.setCertificateEntry(CONJUR_TLS_CA_PATH, certificate);
		final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		SSLContext conjurSSLContext = SSLContext.getInstance(TLS);
		conjurSSLContext.init(null, trustManagerFactory.getTrustManagers(), null);
		return conjurSSLContext;
	}
	
	private Certificate generateCertificate() throws IOException, CertificateException {
		Certificate certificate = null;
		final CertificateFactory certificateFactory = CertificateFactory.getInstance(X_509);
		if (StringUtils.hasLength(CONJUR_SSL_CERTIFICATE)) {
			LOGGER.debug("Using variable CONJUR_SSL_CERTIFICATE: " + CONJUR_SSL_CERTIFICATE);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(CONJUR_SSL_CERTIFICATE.getBytes(StandardCharsets.UTF_8))) {
				certificate = certificateFactory.generateCertificate(inputStream);
			}
		}
		else if (StringUtils.hasLength(CONJUR_CERT_FILE)) {
			LOGGER.debug("Using variable CONJUR_CERT_FILE: " + CONJUR_CERT_FILE);
			try (InputStream inputStream = Files.newInputStream(Paths.get(CONJUR_CERT_FILE))) {
				certificate = certificateFactory.generateCertificate(inputStream);
			}
		}
		return certificate;
	}

	private Request<?> generateRequest() {
		Request<?> request = new DefaultRequest<Void>(AWS_SERVICE_NAME);
		request.setEndpoint(URI.create(AWS_ENDPOINT));
		request.setHttpMethod(HttpMethodName.GET);
		request.addParameter(REQUEST_ACTION, GET_CALLER_IDENTITY);
		request.addParameter(REQUEST_VERSION, GET_CALLER_IDENTITY_VERSION);
		return request;
	}
}
