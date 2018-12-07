package io.target365.service;

import io.target365.client.StrexClient;
import io.target365.client.Target365Client;
import io.target365.dto.OneTimePassword;
import io.target365.dto.StrexMerchantId;
import io.target365.exception.InvalidInputException;
import io.target365.util.Util;
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
    public void test() throws Exception {
        final StrexMerchantId strexMerchantId = new StrexMerchantId()
                .setMerchantId("10000001")
                .setShortNumberId("NO-0000")
                .setPassword("test");

        final OneTimePassword oneTimePassword = new OneTimePassword()
                .setTransactionId(UUID.randomUUID().toString())
                .setMerchantId("10000001")
                .setRecipient("+4798079008")
                .setRecurring(false);

        // Delete strex merchant id if it exists (data cleanup)
        strexClient.getMerchantIds().get().stream().filter(smid -> smid.getMerchantId().equals(strexMerchantId.getMerchantId()))
            .forEach(smid -> Util.wrap(() -> strexClient.deleteMerchantId(smid.getMerchantId()).get()));

        // Create strex merchant id
        strexClient.putMerchantId(strexMerchantId.getMerchantId(), strexMerchantId).get();

        // Create one-time password
        strexClient.postOneTimePassword(oneTimePassword).get();

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
    public void validation() {
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
}
