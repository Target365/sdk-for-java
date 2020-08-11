package io.target365.service;

import io.target365.client.StrexClient;
import io.target365.client.Target365Client;
import io.target365.dto.OneClickConfig;
import io.target365.dto.StrexMerchantId;
import io.target365.dto.StrexOneTimePassword;
import io.target365.dto.StrexTransaction;
import io.target365.dto.enums.UserValidity;
import io.target365.exception.InvalidInputException;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class StrexClientTest extends ClientTest {

    private StrexClient strexClient;

    @Before
    public void before() throws Exception {
        this.strexClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    public void testOneTimePassword() throws Exception {
        final StrexOneTimePassword strexOneTimePassword = new StrexOneTimePassword()
                .setTransactionId(UUID.randomUUID().toString())
                .setMerchantId("10000002")
                .setRecipient("+4798079008")
                .setRecurring(Boolean.FALSE);

        // Create one-time password
        strexClient.postStrexOneTimePassword(strexOneTimePassword).get();

        // Read one-time password
        final StrexOneTimePassword createdStrexOneTimePassword = strexClient.getStrexOneTimePassword(strexOneTimePassword.getTransactionId()).get();
        assertThat(createdStrexOneTimePassword).isNotNull();
        assertThat(createdStrexOneTimePassword.getTransactionId()).isEqualTo(strexOneTimePassword.getTransactionId());
        assertThat(createdStrexOneTimePassword.getMerchantId()).isEqualTo(strexOneTimePassword.getMerchantId());
        assertThat(createdStrexOneTimePassword.getRecipient()).isEqualTo(strexOneTimePassword.getRecipient());
        assertThat(createdStrexOneTimePassword.getRecurring()).isEqualTo(strexOneTimePassword.getRecurring());
    }

    @Test
    public void testTransaction() throws Exception {
        final StrexTransaction strexTransaction = new StrexTransaction()
                .setTransactionId(UUID.randomUUID().toString())
                .setMerchantId("JavaSdkTest")
                .setShortNumber("0000")
                .setRecipient("+4798079008")
                .setPrice(10d)
                .setTimeout(10)
                .setContent("Java SDK Test")
                .setServiceCode("10001")
                .setInvoiceText("Test Invoice Text");

        // Create strex transaction
        strexClient.postStrexTransaction(strexTransaction).get();

        // Read strex transaction
        final StrexTransaction createdStrexTransaction = strexClient.getStrexTransaction(strexTransaction.getTransactionId()).get();
        assertThat(createdStrexTransaction).isNotNull();
        assertThat(createdStrexTransaction.getTransactionId()).isEqualTo(strexTransaction.getTransactionId());
        assertThat(createdStrexTransaction.getMerchantId()).isEqualTo(strexTransaction.getMerchantId());
        assertThat(createdStrexTransaction.getShortNumber()).isEqualTo(strexTransaction.getShortNumber());
        assertThat(createdStrexTransaction.getRecipient()).isEqualTo(strexTransaction.getRecipient());
        assertThat(createdStrexTransaction.getPrice()).isCloseTo(strexTransaction.getPrice(), Percentage.withPercentage(1));
        assertThat(createdStrexTransaction.getTimeout().intValue()).isEqualTo(strexTransaction.getTimeout().intValue());
        assertThat(createdStrexTransaction.getServiceCode()).isEqualTo(strexTransaction.getServiceCode());
        assertThat(createdStrexTransaction.getInvoiceText()).isEqualTo(strexTransaction.getInvoiceText());

        // Reverse strex transaction
        final String reversedStrexTransactionId = strexClient.reverseStrexTransaction(strexTransaction.getTransactionId()).get();

        // Read reversed strex transaction
        final StrexTransaction reversedStrexTransaction = strexClient.getStrexTransaction(reversedStrexTransactionId).get();
        assertThat(reversedStrexTransaction).isNotNull();
        assertThat(reversedStrexTransaction.getTransactionId()).isEqualTo(reversedStrexTransactionId);
        assertThat(reversedStrexTransaction.getMerchantId()).isEqualTo(strexTransaction.getMerchantId());
        assertThat(reversedStrexTransaction.getShortNumber()).isEqualTo(strexTransaction.getShortNumber());
        assertThat(reversedStrexTransaction.getRecipient()).isEqualTo(strexTransaction.getRecipient());
        assertThat(reversedStrexTransaction.getPrice()).isCloseTo(-1 * strexTransaction.getPrice(), Percentage.withPercentage(1));
        assertThat(reversedStrexTransaction.getServiceCode()).isEqualTo(strexTransaction.getServiceCode());
        assertThat(reversedStrexTransaction.getInvoiceText()).isEqualTo(strexTransaction.getInvoiceText());
    }

    @Test
    public void testUserValidity() throws Exception {
        // Get user validity
        final UserValidity userValidity = strexClient.getStrexUserValidity("+4799031520", "JavaSdkTest").get();
        assertThat(userValidity).isEqualTo(UserValidity.Full);
    }

    @Test
    public void testOneClickConfig() throws Exception {
        final OneClickConfig config = new OneClickConfig()
                .setConfigId("APITEST")
                .setShortNumber("0000")
                .setMerchantId("JavaSdkTest")
                .setPrice(99d)
                .setTimeout(10)
                .setBusinessModel("STREX-PAYMENT")
                .setServiceCode("14002")
                .setInvoiceText("Donation test")
                .setOnlineText("Buy directly")
                .setOfflineText("Buy with SMS pin-code")
                .setRedirectUrl("https://tempuri.org/java")
                .setRecurring(false)
                .setRestricted(false)
                .setAge(0);

        // Save one-click config
        strexClient.saveOneClickConfig(config).get();

        // Read one-click config
        final OneClickConfig createdConfig = strexClient.getOneClickConfig(config.getConfigId()).get();
        assertThat(createdConfig).isNotNull();
        assertThat(createdConfig.getConfigId()).isEqualTo(config.getConfigId());
        assertThat(createdConfig.getShortNumber()).isEqualTo(config.getShortNumber());
        assertThat(createdConfig.getMerchantId()).isEqualTo(config.getMerchantId());
        assertThat(createdConfig.getPrice()).isCloseTo(config.getPrice(), Percentage.withPercentage(1));
        assertThat(createdConfig.getTimeout()).isEqualTo(config.getTimeout());
        assertThat(createdConfig.getBusinessModel()).isEqualTo(config.getBusinessModel());
        assertThat(createdConfig.getServiceCode()).isEqualTo(config.getServiceCode());
        assertThat(createdConfig.getInvoiceText()).isEqualTo(config.getInvoiceText());
        assertThat(createdConfig.getOnlineText()).isEqualTo(config.getOnlineText());
        assertThat(createdConfig.getOfflineText()).isEqualTo(config.getOfflineText());
        assertThat(createdConfig.getRedirectUrl()).isEqualTo(config.getRedirectUrl());
        assertThat(createdConfig.isRecurring()).isEqualTo(config.isRecurring());
        assertThat(createdConfig.isRestricted()).isEqualTo(config.isRestricted());
        assertThat(createdConfig.getAge()).isEqualTo(config.getAge());
    }

    @Test
    public void validationMerchantId() {
        final StrexMerchantId strexMerchantIdWithNulls = new StrexMerchantId();
        final StrexMerchantId strexMerchantIdWithBlanks = new StrexMerchantId().setMerchantId("").setShortNumberIds(null);

        assertThat(catchThrowableOfType(() -> strexClient.getMerchantId(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.getMerchantId(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("merchantId may not be null");
    }

    @Test
    public void validationOneTimePassword() {
        final StrexOneTimePassword strexOneTimePasswordWithNulls = new StrexOneTimePassword();

        assertThat(catchThrowableOfType(() -> strexClient.postStrexOneTimePassword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("oneTimePassword may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexOneTimePassword(strexOneTimePasswordWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("oneTimePassword.transactionId may not be null", "oneTimePassword.merchantId may not be null",
                        "oneTimePassword.recipient may not be null", "oneTimePassword.recurring may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexOneTimePassword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexOneTimePassword(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");
    }

    @Test
    public void validationTransaction() {
        final StrexTransaction strexTransactionWithNulls = new StrexTransaction();

        assertThat(catchThrowableOfType(() -> strexClient.postStrexTransaction(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transaction may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.postStrexTransaction(strexTransactionWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transaction.transactionId may not be null", "transaction.merchantId may not be null",
                        "transaction.shortNumber may not be null", "transaction.price may not be null",
                        "transaction.serviceCode may not be null", "transaction.invoiceText may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexTransaction(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.getStrexTransaction(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.reverseStrexTransaction(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");

        assertThat(catchThrowableOfType(() -> strexClient.reverseStrexTransaction(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("transactionId may not be null");
    }
}
