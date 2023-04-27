package org.example;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.cyberark.conjur.api.Conjur;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

/**
 * @author bnasslahsen
 */

@SpringBootApplication
public class Main implements CommandLineRunner {

	private final static String META_DATA_URL = "http://169.254.169.254/latest/meta-data/iam/info";
	private final static String CONJUR_SSL_CERTIFICATE = 	System.getenv().getOrDefault("CONJUR_SSL_CERTIFICATE", null);
	private final static String CONJUR_CERT_FILE =System.getenv().getOrDefault("CONJUR_CERT_FILE", null);
	private final static String CONJUR_PREFIX="host/data/bnl/aws-apps" ;
	private final static String SECRET_KEY = "jwt-apps/secrets/password";

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Override
	public void run(String...args) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		URL queryRoleUrl = new URL(META_DATA_URL);
		BufferedReader reader = new BufferedReader(new InputStreamReader(queryRoleUrl.openStream()));

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append((line));
		}
		
		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Object> arnInfo = objectMapper.readValue(sb.toString(), new TypeReference<Map<String, Object>>() {});
		String arnString = (String) arnInfo.get("InstanceProfileArn");
		String[] arnParts = arnString.split(":");
		String RoleName = arnParts[5].split("/")[1];
		String conjurAuthnLogin = String.format(CONJUR_PREFIX+"/%s/%s", arnParts[4], RoleName);

		final CertificateFactory cf = CertificateFactory.getInstance("X.509");

		final Certificate cert = cf.generateCertificate(getSslInputStream());
		final KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null);
		ks.setCertificateEntry("conjurTlsCaPath", cert);
		final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);
		SSLContext conjurSSLContext = SSLContext.getInstance("TLS");
		conjurSSLContext.init(null, tmf.getTrustManagers(), null);

		String conjurAuthnApiKey = AwsSigv4Example.getConjurAuthnApiKey();
		Conjur conjur = new Conjur(conjurAuthnLogin, conjurAuthnApiKey, conjurSSLContext);
		String secretValue = conjur.variables().retrieveSecret(SECRET_KEY);
		
		System.out.println(secretValue);
	}
	
	private InputStream getSslInputStream() throws IOException {
		InputStream sslInputStream = null;
		if (StringUtils.hasLength(CONJUR_SSL_CERTIFICATE)) {
			sslInputStream = new ByteArrayInputStream(CONJUR_SSL_CERTIFICATE.getBytes(StandardCharsets.UTF_8));
		} else {
			if (StringUtils.hasLength(CONJUR_CERT_FILE))
				sslInputStream = new FileInputStream(CONJUR_CERT_FILE);
		}

		if (sslInputStream != null) {
			sslInputStream.close();
		}
		return sslInputStream;
	}
}