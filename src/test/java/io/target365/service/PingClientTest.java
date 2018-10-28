package io.target365.service;

import io.target365.client.PingClient;
import io.target365.client.Target365Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class PingClientTest extends ClientTest {

    private PingClient pingClient;

    @Before
    public void before() throws Exception {
        this.pingClient = Target365Client.getInstance(getPrivateKeyAsString(),
            new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    public void test() throws Exception {
        assertThat(pingClient.getPing().get()).isEqualTo("\"pong\"");
    }
}
