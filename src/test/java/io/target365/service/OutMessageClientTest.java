package io.target365.service;

import com.google.common.collect.ImmutableList;
import io.target365.client.OutMessageClient;
import io.target365.client.Target365Client;
import io.target365.dto.OutMessage;
import io.target365.dto.OutMessageBatch;
import io.target365.dto.StrexData;
import io.target365.dto.enums.Priority;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class OutMessageClientTest extends ClientTest {

    private OutMessageClient outMessageClient;

    @Before
    public void before() throws Exception {
        this.outMessageClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    public void test() throws Exception {
        final String msisdn = "+4798079008";

        final OutMessage outMessageForBatch = new OutMessage().setSender("OutMessageBatch Sender")
                .setRecipient("+4798079008").setContent("OutMessageBatch 0001")
                .setSendTime(ZonedDateTime.now().plus(1, ChronoUnit.DAYS)).setTransactionId(UUID.randomUUID().toString());
        final OutMessageBatch outMessageBatch = new OutMessageBatch().setItems(ImmutableList.of(outMessageForBatch));

        final OutMessage outMessage = new OutMessage().setSender("OutMessage Sender")
                .setRecipient("+4798079008").setContent("OutMessage 0001")
                .setSendTime(ZonedDateTime.now().plus(1, ChronoUnit.DAYS));

        assertThat(outMessage.getPriority()).isEqualTo(Priority.Normal.name());
        assertThat(outMessage.getDeliveryMode()).isEqualTo(OutMessage.DeliveryMode.AtMostOnce);

        // Prepare msisdns
        outMessageClient.prepareMsisdns(ImmutableList.of(msisdn)).get();

        // Create out message batch
        final List<String> outMessageForBatchTransactionIds = outMessageClient.postOutMessageBatch(outMessageBatch).get();
        assertThat(outMessageForBatchTransactionIds).hasSize(1);
        final String outMessageForBatchTransactionId = outMessageForBatchTransactionIds.get(0);

        // Read out message for batch
        final OutMessage createdOutMessageBatch = outMessageClient.getOutMessage(outMessageForBatchTransactionId).get();
        assertThat(createdOutMessageBatch.getSender()).isEqualTo(outMessageForBatch.getSender());
        assertThat(createdOutMessageBatch.getRecipient()).isEqualTo(outMessageForBatch.getRecipient());
        assertThat(createdOutMessageBatch.getContent()).isEqualTo(outMessageForBatch.getContent());
        assertThat(createdOutMessageBatch.getTransactionId()).isEqualTo(outMessageForBatchTransactionId);

        // Create out message
        final String outMessageTransactionId = outMessageClient.postOutMessage(outMessage).get();

        // Read out message
        final OutMessage createdOutMessage = outMessageClient.getOutMessage(outMessageTransactionId).get();
        assertThat(createdOutMessage.getSender()).isEqualTo(outMessage.getSender());
        assertThat(createdOutMessage.getRecipient()).isEqualTo(outMessage.getRecipient());
        assertThat(createdOutMessage.getContent()).isEqualTo(outMessage.getContent());
        assertThat(createdOutMessage.getTransactionId()).isEqualTo(outMessageTransactionId);

        // Update out message and verify out message was updated
        createdOutMessage.setContent(createdOutMessage.getContent() + "-updated");
        outMessageClient.putOutMessage(createdOutMessage).get();
        final OutMessage updatedOutMessage = outMessageClient.getOutMessage(outMessageTransactionId).get();
        assertThat(updatedOutMessage.getSender()).isEqualTo(createdOutMessage.getSender());
        assertThat(updatedOutMessage.getRecipient()).isEqualTo(createdOutMessage.getRecipient());
        assertThat(updatedOutMessage.getContent()).isEqualTo(createdOutMessage.getContent());
        assertThat(updatedOutMessage.getTransactionId()).isEqualTo(createdOutMessage.getTransactionId());

        // Delete out message for batch and verify that it has been deleted
        outMessageClient.deleteOutMessage(outMessageForBatchTransactionId).get();
        assertThat(outMessageClient.getOutMessage(outMessageForBatchTransactionId).get()).isNull();

        // Delete out message and verify that it has been deleted
        outMessageClient.deleteOutMessage(outMessageTransactionId).get();
        assertThat(outMessageClient.getOutMessage(outMessageTransactionId).get()).isNull();
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> outMessageClient.prepareMsisdns(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("msisdns must not be empty");

        assertThat(catchThrowableOfType(() -> outMessageClient.prepareMsisdns(ImmutableList.of()), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("msisdns must not be empty");

        assertThat(catchThrowableOfType(() -> outMessageClient.prepareMsisdns(new ArrayList<String>() {
            {
                add("");
                add(null);
            }
        }), InvalidInputException.class).getViolations()).containsExactlyInAnyOrder("msisdns.[0] must not be blank", "msisdns.[1] must not be blank");

        final OutMessageBatch outMessageBatchWithNulls = new OutMessageBatch();
        final OutMessageBatch zeroSizeOutMessageBatch = new OutMessageBatch().setItems(ImmutableList.of());
        final OutMessageBatch hundredAndOneSizeOutMessageBatch = new OutMessageBatch().setItems(IntStream.range(0, 101)
                .mapToObj(i -> new OutMessage().setSender("sender").setRecipient("recepient").setContent("content")).collect(Collectors.toList()));
        final OutMessageBatch outMessageBatchWithNullAndInvalidOutMessages = new OutMessageBatch().setItems(new ArrayList<OutMessage>() {
            {
                add(null);
                add(new OutMessage().setTimeToLive(1500));
            }
        });

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch must not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(outMessageBatchWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch.items must not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(zeroSizeOutMessageBatch), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch.items size must be between 1 and 100");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(hundredAndOneSizeOutMessageBatch), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch.items size must be between 1 and 100");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(outMessageBatchWithNullAndInvalidOutMessages), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch.items[0].<list element> must not be null", "outMessageBatch.items[1].content must not be blank",
                        "outMessageBatch.items[1].recipient must not be blank", "outMessageBatch.items[1].sender must not be blank",
                        "outMessageBatch.items[1].timeToLive must be between 5 and 1440");

        final OutMessage outMessageWithNulls = new OutMessage();
        final OutMessage outMessageWithBlanks = new OutMessage().setSender("").setRecipient("").setContent("").setTimeToLive(0);

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage must not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.sender must not be blank", "outMessage.recipient must not be blank",
                        "outMessage.content must not be blank");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithBlanks), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.sender must not be blank", "outMessage.recipient must not be blank",
                        "outMessage.content must not be blank", "outMessage.timeToLive must be between 5 and 1440");

        assertThat(catchThrowableOfType(() -> outMessageClient.getOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> outMessageClient.getOutMessage(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> outMessageClient.putOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.transactionId must not be blank", "outMessage must not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.putOutMessage(outMessageWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.transactionId must not be blank", "outMessage.sender must not be blank", "outMessage.recipient must not be blank",
                        "outMessage.content must not be blank");

        assertThat(catchThrowableOfType(() -> outMessageClient.putOutMessage(outMessageWithBlanks), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.transactionId must not be blank", "outMessage.sender must not be blank", "outMessage.recipient must not be blank",
                        "outMessage.content must not be blank", "outMessage.timeToLive must be between 5 and 1440");

        assertThat(catchThrowableOfType(() -> outMessageClient.deleteOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> outMessageClient.deleteOutMessage(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        final OutMessage outMessageWithStrexDataWithNulls = new OutMessage().setSender("Sender").setContent("Content")
                .setRecipient("Recipient").setStrex(new StrexData());
        final OutMessage outMessageWithStrexDataWithBlanks = new OutMessage().setSender("Sender").setContent("Content")
                .setRecipient("Recipient").setStrex(new StrexData().setMerchantId("").setServiceCode("").setInvoiceText("").setPrice(1000d));

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithStrexDataWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.strex.merchantId must not be blank", "outMessage.strex.serviceCode must not be blank",
                        "outMessage.strex.invoiceText must not be blank", "outMessage.strex.price must not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithStrexDataWithBlanks), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.strex.merchantId must not be blank", "outMessage.strex.serviceCode must not be blank",
                        "outMessage.strex.invoiceText must not be blank");
    }
}
