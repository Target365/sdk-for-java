package io.target365.service;

import com.google.common.collect.ImmutableList;
import io.target365.client.OutMessageClient;
import io.target365.client.Target365Client;
import io.target365.dto.OutMessage;
import io.target365.dto.OutMessageBatch;
import io.target365.dto.Pincode;
import io.target365.dto.StrexData;
import io.target365.dto.enums.DeliveryMode;
import io.target365.dto.enums.Priority;
import io.target365.exception.InvalidInputException;
import okio.BufferedSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.validation.ValidationException;
import java.io.InputStream;
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
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest2024"));
    }

    @Test
    public void test() throws Exception {
        final String msisdn = "+4798079008";

        final OutMessage outMessageForBatch = new OutMessage()
                .setSender("Target365")
                .setRecipient("+4798079008")
                .setContent("OutMessageBatch 0001")
                .setSendTime(ZonedDateTime.now().plus(1, ChronoUnit.DAYS))
                .setTransactionId(UUID.randomUUID().toString());

        final OutMessageBatch outMessageBatch = new OutMessageBatch().setItems(ImmutableList.of(outMessageForBatch));

        final OutMessage outMessage = new OutMessage()
                .setSender("Target365")
                .setRecipient("+4798079008")
                .setContent("OutMessage 0001")
                .setSendTime(ZonedDateTime.now().plus(1, ChronoUnit.DAYS));

        assertThat(outMessage.getPriority()).isEqualTo(Priority.Normal.name());
        assertThat(outMessage.getDeliveryMode()).isEqualTo(DeliveryMode.AtMostOnce.name());

        // Prepare msisdns
        outMessageClient.prepareMsisdns(ImmutableList.of(msisdn)).get();

        // Create out message batch
        final List<String> outMessageForBatchTransactionIds = outMessageClient.postOutMessageBatch(outMessageBatch).get();
        assertThat(outMessageForBatchTransactionIds).hasSize(1);
        final String outMessageForBatchTransactionId = outMessageForBatchTransactionIds.get(0);

        // Read out message for batch
        final ZonedDateTime timeout = ZonedDateTime.now().plusSeconds(30);
        OutMessage createdOutMessageBatch = null;

        while (ZonedDateTime.now().isBefore(timeout)) {
            createdOutMessageBatch = outMessageClient.getOutMessage(outMessageForBatchTransactionId).get();

            if (createdOutMessageBatch != null)
                break;

            Thread.sleep(2000);
        }

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

        // Out-message export
        String csv = outMessageClient.getOutMessageExport(ZonedDateTime.now().minusDays(3), ZonedDateTime.now().minusDays(2)).get();
        assertThat(csv).isNotNull();
    }

    @Test
    public void validation() {
        // getSmsPartsForText
        String singleSmsText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
        String doubleSmsText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus at velit eget nisl facilisis tempus. Pellentesque consectetur mi in libero maximus tristique. Quisque non nisi volutpat, egestas dui quis, varius nunc. Maecenas turpis libero, tincidunt vitae erat at, accumsan euismod purus.";
        assertThat(OutMessage.getSmsPartsForText(singleSmsText, false)).isEqualTo(1);
        assertThat(OutMessage.getSmsPartsForText(doubleSmsText, false)).isEqualTo(2);
        assertThat(OutMessage.getSmsPartsForText(singleSmsText, true)).isEqualTo(1);
        assertThat(OutMessage.getSmsPartsForText(doubleSmsText, true)).isEqualTo(5);

        assertThat(catchThrowableOfType(() -> outMessageClient.prepareMsisdns(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("msisdns must not be empty");

        assertThat(catchThrowableOfType(() -> outMessageClient.prepareMsisdns(ImmutableList.of()), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("msisdns must not be empty");

        assertThat(catchThrowableOfType(() -> outMessageClient.prepareMsisdns(new ArrayList<String>() {
            {
                add("");
                add(null);
            }
        }), InvalidInputException.class).getViolations()).containsExactlyInAnyOrder("msisdns.[0] may not be null", "msisdns.[1] may not be null");

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
                .containsExactlyInAnyOrder("outMessageBatch may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(outMessageBatchWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch.items may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessageBatch(outMessageBatchWithNullAndInvalidOutMessages), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessageBatch.items[0].<collection element> may not be null", "outMessageBatch.items[1].content may not be null",
                        "outMessageBatch.items[1].recipient may not be null", "outMessageBatch.items[1].sender may not be null",
                        "outMessageBatch.items[1].timeToLive must be less than or equal to 1440");

        final OutMessage outMessageWithNulls = new OutMessage();
        final OutMessage outMessageWithZeroTimeToLive = new OutMessage().setTimeToLive(0);

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.sender may not be null", "outMessage.recipient may not be null",
                        "outMessage.content may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithZeroTimeToLive), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.sender may not be null", "outMessage.recipient may not be null",
                        "outMessage.content may not be null", "outMessage.timeToLive must be greater than or equal to 5");

        assertThat(catchThrowableOfType(() -> outMessageClient.getOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.getOutMessage(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.putOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.transactionId may not be null", "outMessage may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.putOutMessage(outMessageWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.transactionId may not be null", "outMessage.sender may not be null", "outMessage.recipient may not be null",
                        "outMessage.content may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.putOutMessage(outMessageWithZeroTimeToLive), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.transactionId may not be null", "outMessage.sender may not be null", "outMessage.recipient may not be null",
                        "outMessage.content may not be null", "outMessage.timeToLive must be greater than or equal to 5");

        assertThat(catchThrowableOfType(() -> outMessageClient.deleteOutMessage(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> outMessageClient.deleteOutMessage(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        final OutMessage outMessageWithStrexDataWithNulls = new OutMessage().setSender("Sender").setContent("Content")
                .setRecipient("Recipient").setStrex(new StrexData());

        assertThat(catchThrowableOfType(() -> outMessageClient.postOutMessage(outMessageWithStrexDataWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("outMessage.strex.merchantId may not be null", "outMessage.strex.serviceCode may not be null",
                        "outMessage.strex.invoiceText may not be null", "outMessage.strex.price may not be null");
    }
}
