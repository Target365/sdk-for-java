package io.target365.service;

import io.target365.client.Target365Client;
import io.target365.client.VerificationClient;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class VerificationClientTest extends ClientTest {

    private VerificationClient verificationClient;

    @Before
    public void before() throws Exception {
        this.verificationClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    @Ignore("Need to have a message encrypted with a private key, which could be verified by a server public key")
    public void test() throws Exception {
        // Reverse payment
        verificationClient.verifySignature(null, null, null, null).get();
    }

    @Test
    public void validation() throws Exception {
        assertThat(catchThrowableOfType(() -> verificationClient.verifySignature(null, null, null, null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("method must not be blank", "uri must not be blank", "content must not be null",
                        "xEcdsaSignatureString must not be blank", "xEcdsaSignatureString must conform to the pattern ^[A-Za-z0-9_-]+:[0-9]+:[A-Za-z0-9_-]+:[A-Za-z0-9_+/=]+$");

        assertThat(catchThrowableOfType(() -> verificationClient.verifySignature("", "", "", ""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("method must not be blank", "uri must not be blank", "xEcdsaSignatureString must not be blank",
                        "xEcdsaSignatureString must conform to the pattern ^[A-Za-z0-9_-]+:[0-9]+:[A-Za-z0-9_-]+:[A-Za-z0-9_+/=]+$");

        assertThat(catchThrowableOfType(() -> verificationClient.verifySignature("", "", "", ""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("method must not be blank", "uri must not be blank", "xEcdsaSignatureString must not be blank",
                        "xEcdsaSignatureString must conform to the pattern ^[A-Za-z0-9_-]+:[0-9]+:[A-Za-z0-9_-]+:[A-Za-z0-9_+/=]+$");

        assertThat(catchThrowableOfType(() -> verificationClient.verifySignature("GET", "uri", "", ":::"), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("xEcdsaSignatureString must conform to the pattern ^[A-Za-z0-9_-]+:[0-9]+:[A-Za-z0-9_-]+:[A-Za-z0-9_+/=]+$");

        // Verify that timestamp consists of digits only
        assertThat(catchThrowableOfType(() -> verificationClient.verifySignature("GET", "uri", "", ":abc::"), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("xEcdsaSignatureString must conform to the pattern ^[A-Za-z0-9_-]+:[0-9]+:[A-Za-z0-9_-]+:[A-Za-z0-9_+/=]+$");

        // Verify that there is no clock drift
        final String sign = new EcdsaAuthorizationService().signHeader(EcdsaSigner.getInstance(getPrivateKeyAsString()), "TestKey", "GET", "http://test.com", "");
        // Replace a timestamp with the one in the past
        final String[] parts = sign.replaceAll("HMAC ", "").split(":");
        final String clockDriftedSign = parts[0] + ":" + ZonedDateTime.now().minus(1, ChronoUnit.DAYS).toEpochSecond() + ":" + parts[2] + ":" + parts[3];

        assertThat(catchThrowableOfType(() -> verificationClient.verifySignature("GET", "http://test.com", "", clockDriftedSign), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("timestamp clock-drift too big");
    }
}
