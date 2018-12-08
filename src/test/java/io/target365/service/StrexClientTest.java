package io.target365.service;

import io.target365.client.Client;
import io.target365.client.OutMessageClient;
import io.target365.client.StrexClient;
import io.target365.client.Target365Client;
import io.target365.dto.StrexMerchantId;
import io.target365.dto.StrexOneTimePassword;
import io.target365.dto.StrexTransaction;
import io.target365.exception.InvalidInputException;
import io.target365.util.Util;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class StrexClientTest extends ClientTest {

    private StrexClient strexClient;
    private OutMessageClient outMessageClient;

    @Before
    public void before() throws Exception {
        final Client client = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));

        this.strexClient = client;
        this.outMessageClient = client;
    }

    @Test
    public void testMerchantId() throws Exception {
        final StrexMerchantId strexMerchantId = new StrexMerchantId()
                .setMerchantId("10000001")
                .setShortNumberId("NO-0000")
                .setPassword("test");

        // Delete strex merchant id if it exists (data cleanup)
        strexClient.getMerchantIds().get().stream().filter(smid -> smid.getMerchantId().equals(strexMerchantId.getMerchantId()))
                .forEach(smid -> Util.wrap(() -> strexClient.deleteMerchantId(smid.getMerchantId()).get()));

        // Create strex merchant id
        strexClient.putMerchantId(strexMerchantId.getMerchantId(), strexMerchantId).get();

        // Read strex merchant id
        final StrexMerchantId created = strexClient.getMerchantId(strexMerchantId.getMerchantId()).get();
        assertThat(created.getMerchantId()).isEqualTo(strexMerchantId.getMerchantId());
        assertThat(created.getShortNumberId()).isEqualTo(strexMerchantId.getShortNumberId());
        assertThat(created.getPassword()).isEqualTo(null);

        // Update strex merchant id verify strex merchant id was updated
        strexClient.putMerchantId(created.getMerchantId(), new StrexMerchantId().setMerchantId(created.getMerchantId())
                .setShortNumberId(created.getShortNumberId()).setPassword(created.getPassword() + "-updated")).get();
        final StrexMerchantId updated = strexClient.getMerchantId(created.getMerchantId()).get();
        assertThat(updated.getMerchantId()).isEqualTo(strexMerchantId.getMerchantId());
        assertThat(updated.getShortNumberId()).isEqualTo(strexMerchantId.getShortNumberId());
        assertThat(updated.getPassword()).isEqualTo(null);

        // Delete strex merchant id and verify that it has been deleted
        strexClient.deleteMerchantId(created.getMerchantId()).get();
        assertThat(strexClient.getMerchantId(strexMerchantId.getMerchantId()).get()).isNull();
    }

    @Test
    @Ignore("Waitig for implementation to be ready")
    public void testOneTimePassword() throws Exception {
        final String transactionId = UUID.randomUUID().toString();

        final StrexOneTimePassword strexOneTimePassword = new StrexOneTimePassword()
                .setTransactionId(transactionId)
                .setMerchantId("10000001")
                .setRecipient("+4798079008")
                .setRecurring(false);

        // Create one-time password
        strexClient.postStrexOneTimePassword(strexOneTimePassword).get();

        // Read one-time password
        final StrexOneTimePassword createdStrexOneTimePassword = strexClient.getStrexOneTimePassword(transactionId).get();
        assertThat(createdStrexOneTimePassword).isNotNull();
    }

    @Test
    @Ignore("Waitig for implementation to be ready")
    public void testTransaction() throws Exception {
        final String transactionId = UUID.randomUUID().toString();

        final StrexTransaction strexTransaction = new StrexTransaction()
                .setTransactionId(transactionId)
                .setMerchantId("10000001")
                .setShortNumber("NO-0000")
                .setRecipient("+4798079008")
                .setPrice(1000d)
                .setServiceCode("10001")
                .setInvoiceText("Test Invoice Text");

        // Create strex transaction
        strexClient.postStrexTransaction(strexTransaction).get();

        // Read strex transaction
        final StrexTransaction createdStrexTransaction = strexClient.getStrexTransaction(transactionId).get();
        assertThat(createdStrexTransaction).isNotNull();

        // Reverse strex transaction
        strexClient.reverseStrexTransaction(transactionId).get();

        // Read strex transaction
        final StrexTransaction reversedStrexTransaction = strexClient.getStrexTransaction(transactionId).get();
        assertThat(reversedStrexTransaction).isNotNull();
    }

    @Test
    public void validationMerchantId() {
        final StrexMerchantId strexMerchantIdWithNulls = new StrexMerchantId();
        final StrexMerchantId strexMerchantIdWithBlanks = new StrexMerchantId().setMerchantId("").setShortNumberId("");

        assertThat(catchThrowableOfType(() -> strexClient.getMerchantId(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.getMerchantId(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.putMerchantId(null, null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank", "strexMerchantId must not be null");

        assertThat(catchThrowableOfType(() -> strexClient.putMerchantId("", strexMerchantIdWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank", "strexMerchantId.merchantId must not be blank",
                        "strexMerchantId.shortNumberId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.putMerchantId("", strexMerchantIdWithBlanks), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank", "strexMerchantId.merchantId must not be blank",
                        "strexMerchantId.shortNumberId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.deleteMerchantId(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.deleteMerchantId(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId must not be blank");
    }

    @Test
    public void validationOneTimePassword() {
        final StrexOneTimePassword strexOneTimePasswordWithNulls = new StrexOneTimePassword();
        final StrexOneTimePassword strexOneTimePasswordWithBlanks = new StrexOneTimePassword().setTransactionId("")
                .setMerchantId("").setRecipient("").setRecurring(Boolean.FALSE);

        assertThat(catchThrowableOfType(() -> strexClient.postStrexOneTimePassword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("oneTimePassword must not be null");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexOneTimePassword(strexOneTimePasswordWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("oneTimePassword.transactionId must not be blank", "oneTimePassword.merchantId must not be blank",
                        "oneTimePassword.recipient must not be blank", "oneTimePassword.recurring must not be null");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexOneTimePassword(strexOneTimePasswordWithBlanks), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("oneTimePassword.transactionId must not be blank", "oneTimePassword.merchantId must not be blank",
                        "oneTimePassword.recipient must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexOneTimePassword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexOneTimePassword(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");
    }

    @Test
    public void validationTransaction() {
        final StrexTransaction strexTransactionWithNulls = new StrexTransaction();
        final StrexTransaction strexTransactionWithBlanks = new StrexTransaction().setTransactionId("")
                .setMerchantId("").setRecipient("").setShortNumber("").setRecipient("").setPrice(1000d).setServiceCode("").setInvoiceText("");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexTransaction(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transaction must not be null");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexTransaction(strexTransactionWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transaction.transactionId must not be blank", "transaction.merchantId must not be blank",
                        "transaction.shortNumber must not be blank", "transaction.recipient must not be blank",
                        "transaction.price must not be null", "transaction.serviceCode must not be blank",
                        "transaction.invoiceText must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexTransaction(strexTransactionWithBlanks), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transaction.transactionId must not be blank", "transaction.merchantId must not be blank",
                        "transaction.shortNumber must not be blank", "transaction.recipient must not be blank",
                        "transaction.serviceCode must not be blank", "transaction.invoiceText must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexTransaction(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexTransaction(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.reverseStrexTransaction(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");

        assertThat(catchThrowableOfType(() -> strexClient.reverseStrexTransaction(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId must not be blank");
    }
}
