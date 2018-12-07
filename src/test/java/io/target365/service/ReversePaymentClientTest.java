package io.target365.service;

import io.target365.client.*;
import io.target365.dto.OutMessage;
import io.target365.dto.StrexData;
import io.target365.dto.StrexMerchantId;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class ReversePaymentClientTest extends ClientTest {

    private ReversePaymentClient reversePaymentClient;
    private OutMessageClient outMessageClient;
    private StrexClient strexClient;

    @Before
    public void before() throws Exception {
        final Client client = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));

        this.reversePaymentClient = client;
        this.outMessageClient = client;
        this.strexClient = client;
    }

    @Test
    @Ignore("Message reverse fail with message {\"Message\":\"transaction id 'be1a1806-960a-45cc-98a2-fad6c8d7c2d8' hasn't been billed/processed and can't be reversed.\"}")
    public void test() throws Exception {
        final StrexMerchantId strexMerchantId = new StrexMerchantId()
                .setMerchantId("10000002").setShortNumberId("NO-0000").setPassword("test");

        // Create strex merchant id
        strexClient.putMerchantId(strexMerchantId.getMerchantId(), strexMerchantId).get();

        final OutMessage outMessage = new OutMessage().setSender("OutMessage Sender")
                .setRecipient("+4798079008").setContent("OutMessage 0001")
                .setStrex(new StrexData().setMerchantId(strexMerchantId.getMerchantId())
                        .setPrice(1000d).setServiceCode("10001").setInvoiceText("Test Invoice Text"));

        // Create out message payment
        final String outMessageTransactionId = outMessageClient.postOutMessage(outMessage).get();

        // Reverse payment
        final String reversePaymentTransactionId = reversePaymentClient.reversePayment(outMessageTransactionId).get();
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> reversePaymentClient.reversePayment(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> reversePaymentClient.reversePayment(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");
    }
}
