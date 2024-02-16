package io.target365.service;

import io.target365.client.PublicKeysClient;
import io.target365.client.Target365Client;
import io.target365.dto.PublicKey;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class PublicKeyClientTest extends ClientTest {

    private static final String PUBLIC_KEY_NAME = "JavaSdkTest2024";
    private static final String PUBLIC_KEY_STRING = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAESYVYv1nzWz/Kljo/+g5FK1tC5wkMItmYkWHiM++FDsDYszl07Fb2aXJe8/0iXgYShJZ6pMZN0HoOz0OqZDliyQ==";
    private static final String PUBLIC_KEY_SIGN_ALGO = "ECDsaP256";
    private static final String PUBLIC_KEY_HASH_ALGO = "SHA256";

    private PublicKeysClient publicKeysClient;

    @Before
    public void before() throws Exception {
        this.publicKeysClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest2024"));
    }

    @Test
    public void test() throws Exception {
        // TODO This method was never tested, as in order to invoke it, need to know name of the server public key
        // Read server public key
//        final PublicKey serverPublicKey = publicKeysClient.getServerPublicKey("").get();

        // Read all client public keys
        final List<PublicKey> clientPublicKeys = publicKeysClient.getClientPublicKeys().get();
        assertThat(clientPublicKeys).isNotEmpty();
        assertThat(clientPublicKeys).extracting(PublicKey::getName).contains(PUBLIC_KEY_NAME);
        assertThat(clientPublicKeys).filteredOn(pk -> PUBLIC_KEY_NAME.equalsIgnoreCase(pk.getName()))
                .extracting(PublicKey::getPublicKeyString).containsOnly(PUBLIC_KEY_STRING);
        assertThat(clientPublicKeys).filteredOn(pk -> PUBLIC_KEY_NAME.equalsIgnoreCase(pk.getName()))
                .extracting(PublicKey::getSignAlgo).containsOnly(PUBLIC_KEY_SIGN_ALGO);
        assertThat(clientPublicKeys).filteredOn(pk -> PUBLIC_KEY_NAME.equalsIgnoreCase(pk.getName()))
                .extracting(PublicKey::getHashAlgo).containsOnly(PUBLIC_KEY_HASH_ALGO);

        // Read JavaSdkTest client public key
        final PublicKey clientPublicKey = publicKeysClient.getClientPublicKey(PUBLIC_KEY_NAME).get();
        assertThat(clientPublicKey).isNotNull();
        assertThat(clientPublicKey).extracting(PublicKey::getPublicKeyString).isEqualTo(PUBLIC_KEY_STRING);
        assertThat(clientPublicKey).extracting(PublicKey::getSignAlgo).isEqualTo(PUBLIC_KEY_SIGN_ALGO);
        assertThat(clientPublicKey).extracting(PublicKey::getHashAlgo).isEqualTo(PUBLIC_KEY_HASH_ALGO);

        // TODO This method was never tested, as if to delete client public key, there is not way to add it back easily
        // Delete JavaSdkTest client public key
//        publicKeysClient.deleteClientPublicKey(PUBLIC_KEY_NAME).get();
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> publicKeysClient.getServerPublicKey(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyName may not be null");

        assertThat(catchThrowableOfType(() -> publicKeysClient.getServerPublicKey(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyName may not be null");

        assertThat(catchThrowableOfType(() -> publicKeysClient.getClientPublicKey(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyName may not be null");

        assertThat(catchThrowableOfType(() -> publicKeysClient.getClientPublicKey(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyName may not be null");

        assertThat(catchThrowableOfType(() -> publicKeysClient.deleteClientPublicKey(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyName may not be null");

        assertThat(catchThrowableOfType(() -> publicKeysClient.deleteClientPublicKey(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyName may not be null");
    }
}
