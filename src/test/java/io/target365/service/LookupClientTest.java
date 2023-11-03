package io.target365.service;

import io.target365.client.LookupClient;
import io.target365.client.Target365Client;
import io.target365.dto.LookupResult;
import io.target365.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class LookupClientTest extends ClientTest {

    private LookupClient lookupClient;

    @Before
    public void before() throws Exception {
        this.lookupClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    public void testAddressLookup() throws Exception {
        final String msisdn = "+4798079008";

        // Lookup client
        final LookupResult lookupResult = lookupClient.addressLookup(msisdn).get();
        assertThat(lookupResult).isNotNull();
        assertThat(lookupResult.getMsisdn()).isEqualTo(msisdn);
        assertThat(lookupResult.getFirstName()).isEqualTo("Hans");
        assertThat(lookupResult.getMiddleName()).isEqualTo("Olav");
        assertThat(lookupResult.getLastName()).isEqualTo("Stjernholm");
        assertThat(lookupResult.getGender()).isEqualTo(LookupResult.Gender.M);
    }

    @Test
    public void testFreetextLookup() throws Exception {
        final String freetext = "+4798079008";

        // Lookup client
        final LookupResult[] lookupResults = lookupClient.freetextLookup(freetext).get();
        assertThat(lookupResults).isNotEmpty();
        final LookupResult first = lookupResults[0];
        assertThat(first).isNotNull();
        assertThat(first.getMsisdn()).isEqualTo(freetext);
        assertThat(first.getFirstName()).isEqualTo("Hans");
        assertThat(first.getMiddleName()).isEqualTo("Olav");
        assertThat(first.getLastName()).isEqualTo("Stjernholm");
        assertThat(first.getGender()).isEqualTo(LookupResult.Gender.M);
    }

    @Test
    public void validation() {
        assertThat(catchThrowableOfType(() -> lookupClient.addressLookup(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("msisdn may not be null");

        assertThat(catchThrowableOfType(() -> lookupClient.addressLookup(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("msisdn may not be null");
    }
}
