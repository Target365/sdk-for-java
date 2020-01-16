package io.target365.service;

import io.target365.client.InMessageClient;
import io.target365.client.Target365Client;
import io.target365.dto.InMessage;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
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
    public void test() throws Exception {
        // Read in message
        final InMessage inMessage = inMessageClient.getInMessage("NO-0000", "79f35793-6d70-423c-a7f7-ae9fb1024f3b").get();
        assertThat(inMessage).isNotNull();
        assertThat(inMessage.getTransactionId()).isEqualTo("79f35793-6d70-423c-a7f7-ae9fb1024f3b");
        assertThat(inMessage.getKeywordId()).isEqualTo("102");
        assertThat(inMessage.getSender()).isEqualTo("+4798079008");
        assertThat(inMessage.getRecipient()).isEqualTo("0000");
        assertThat(inMessage.getContent()).isEqualTo("Test");
        assertThat(inMessage.getIsStopMessage()).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> inMessageClient.getInMessage(null, null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("shortNumberId may not be null", "transactionId may not be null");

        assertThat(catchThrowableOfType(() -> inMessageClient.getInMessage("", ""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("shortNumberId may not be null", "transactionId may not be null");
    }
}
