package io.target365.service;

import io.target365.client.OutMessageClient;
import io.target365.client.PincodeClient;
import io.target365.client.Target365Client;
import io.target365.dto.OutMessage;
import io.target365.dto.Pincode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class PincodeClientTest extends ClientTest {

    private PincodeClient pincodeClient;
    private OutMessageClient outMessageClient;

    @Before
    public void before() throws Exception {
        this.pincodeClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest2024"));

        this.outMessageClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest2024"));
    }

    @Test
    public void testSendPincode() throws Exception {
        final Pincode pincode = new Pincode()
                .setTransactionId(UUID.randomUUID().toString())
                .setRecipient("+4798079008")
                .setSender("Target365")
                .setMaxAttempts(3);

        // Create pin code and send sms
        pincodeClient.postPincode(pincode).get();

        // Read out-message
        final OutMessage createdOutMessage = outMessageClient.getOutMessage(pincode.getTransactionId()).get();
        assertThat(createdOutMessage).isNotNull();
        assertThat(createdOutMessage.getTransactionId()).isEqualTo(pincode.getTransactionId());
    }
}
