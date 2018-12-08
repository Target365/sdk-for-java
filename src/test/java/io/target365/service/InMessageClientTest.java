package io.target365.service;

import io.target365.client.InMessageClient;
import io.target365.client.Target365Client;
import io.target365.dto.InMessage;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class InMessageClientTest extends ClientTest {

    private InMessageClient inMessageClient;

    @Before
    public void before() throws Exception {
        this.inMessageClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    @Ignore("Need to know shortNumberId and transactionId of in-message in order to get it")
    public void test() throws Exception {
        // Read in message
        final InMessage inMessage = inMessageClient.getInMessage(null, null).get();
        assertThat(inMessage).isNotNull();
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> inMessageClient.getInMessage(null, null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("shortNumberId must not be blank", "transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> inMessageClient.getInMessage("", ""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("shortNumberId must not be blank", "transactionId must not be blank");
    }
}
