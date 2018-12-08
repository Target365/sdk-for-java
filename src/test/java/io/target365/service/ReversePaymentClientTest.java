package io.target365.service;

import io.target365.client.Client;
import io.target365.client.OutMessageClient;
import io.target365.client.ReversePaymentClient;
import io.target365.client.Target365Client;
import io.target365.dto.OutMessage;
import io.target365.dto.StrexData;
import io.target365.exception.InvalidInputException;
import org.awaitility.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.ZonedDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.awaitility.Awaitility.given;

@RunWith(JUnit4.class)
public class ReversePaymentClientTest extends ClientTest {

    private ReversePaymentClient reversePaymentClient;
    private OutMessageClient outMessageClient;

    @Before
    public void before() throws Exception {
        final Client client = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));

        this.reversePaymentClient = client;
        this.outMessageClient = client;
    }

    @Test
    public void test() throws Exception {
        final OutMessage outMessage = new OutMessage().setSender("OutMessage Sender")
                .setRecipient("+4798079008").setContent("OutMessage 0001")
                .setSendTime(ZonedDateTime.now())
                .setStrex(new StrexData().setMerchantId("10000001").setPrice(1000d)
                        .setServiceCode("10001").setInvoiceText("Test Invoice Text"));

        // Create out message
        final String outMessageTransactionId = outMessageClient.postOutMessage(outMessage).get();

        // Reverse payment
        final String reversedOutMessageTransactionId = given().ignoreExceptions()
                .pollInterval(Duration.ONE_SECOND).atMost(Duration.TEN_SECONDS).await("Until out-message is billed/processed")
                .until(() -> reversePaymentClient.reversePayment(outMessageTransactionId).get(), Objects::nonNull);

        assertThat(reversedOutMessageTransactionId).isNotNull();
        assertThat(reversedOutMessageTransactionId).isEqualTo("-" + outMessageTransactionId);
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> reversePaymentClient.reversePayment(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> reversePaymentClient.reversePayment(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");
    }
}
