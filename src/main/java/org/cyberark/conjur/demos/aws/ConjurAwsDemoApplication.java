package org.cyberark.conjur.demos.aws;

import javax.net.ssl.SSLContext;

import com.cyberark.conjur.api.Conjur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.function.Function;

import static org.cyberark.conjur.demos.aws.Constants.CONJUR_SECRET_KEY;

/**
 * @author bnasslahsen
 */
@SpringBootApplication
public class ConjurAwsDemoApplication{

	private static final Logger LOGGER = LoggerFactory.getLogger(ConjurAwsDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConjurAwsDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ConjurAwsService conjurAwsService) {
		return (args) -> {
			String conjurAuthnLogin = conjurAwsService.getConjurAuthnLogin();
			String conjurAuthnApiKey = conjurAwsService.getConjurAuthnApiKey();
			SSLContext conjurSSLContext = conjurAwsService.createSslContext();
			Conjur conjur = new Conjur(conjurAuthnLogin, conjurAuthnApiKey, conjurSSLContext);
			String secretValue = conjur.variables().retrieveSecret(CONJUR_SECRET_KEY);
			LOGGER.info("Secret Value: {}", secretValue);
		};
	}

	@Bean
	public Function<String, String> uppercase(ConjurAwsService conjurAwsService) {
		return value -> commandLineRunner(conjurAwsService).toString().toUpperCase();
	}
}
