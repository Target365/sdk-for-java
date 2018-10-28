package io.target365.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class SignAndVerifyTest extends ClientTest {

    private Signer signer;
    private Verifier verifier;

    @Before
    public void before() throws Exception {
        this.signer = EcdsaSigner.getInstance(getPrivateKeyAsString());
        this.verifier = EcdsaVerifier.getInstance(getPublicKeyAsString());
    }

    @Test
    public void test() {
        final String message = "Test Message";

        assertThat(verifier.verify(message, signer.sign(message))).isTrue();
    }
}
