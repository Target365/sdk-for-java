package io.target365.service;

import io.target365.client.KeywordClient;
import io.target365.client.Target365Client;
import io.target365.dto.Keyword;
import io.target365.exception.InvalidInputException;
import io.target365.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@RunWith(JUnit4.class)
public class KeywordClientTest extends ClientTest {

    private KeywordClient keywordClient;

    @Before
    public void before() throws Exception {
        this.keywordClient = Target365Client.getInstance(getPrivateKeyAsString(),
                new Target365Client.Parameters("https://test.target365.io/", "JavaSdkTest"));
    }

    @Test
    public void test() throws Exception {
        final Keyword keyword = new Keyword()
                .setShortNumberId("NO-0000")
                .setKeywordText("java-sdk-test-keyword-text-0001")
                .setMode(Keyword.Mode.Text)
                .setForwardUrl("https://www.java-sdk-test-keyword-text-0001.com")
                .setEnabled(Boolean.TRUE);

        // Delete keyword if it exists (data cleanup)
        keywordClient.getKeywords(null, keyword.getKeywordText(), null, null).get()
                .forEach(k -> Util.wrap(() -> keywordClient.deleteKeyword(k.getKeywordId()).get()));

        // Create keyword
        final String keywordId = keywordClient.postKeyword(keyword).get();

        // Read keyword
        final Keyword created = keywordClient.getKeyword(keywordId).get();
        assertThat(created.getShortNumberId()).isEqualTo(keyword.getShortNumberId());
        assertThat(created.getKeywordText()).isEqualTo(keyword.getKeywordText());
        assertThat(created.getMode()).isEqualTo(keyword.getMode());
        assertThat(created.getForwardUrl()).isEqualTo(keyword.getForwardUrl());
        assertThat(created.getEnabled()).isEqualTo(keyword.getEnabled());

        // Update keyword
        keywordClient.putKeyword(new Keyword().setKeywordId(keywordId)
                .setShortNumberId(created.getShortNumberId()).setKeywordText(created.getKeywordText()).setMode(created.getMode())
                .setForwardUrl(created.getForwardUrl() + "-updated").setEnabled(created.getEnabled())).get();

        // Read keywords
        final Keyword updated = keywordClient.getKeywords(null, created.getKeywordText(), null, null).get()
                .stream().filter(k -> k.getKeywordText().equalsIgnoreCase(keyword.getKeywordText())).findAny().get();
        assertThat(updated.getShortNumberId()).isEqualTo(keyword.getShortNumberId());
        assertThat(updated.getKeywordText()).isEqualTo(keyword.getKeywordText());
        assertThat(updated.getMode()).isEqualTo(keyword.getMode());
        assertThat(updated.getForwardUrl()).isEqualTo(keyword.getForwardUrl() + "-updated");
        assertThat(updated.getEnabled()).isEqualTo(keyword.getEnabled());

        // Delete keyword and verify that it has been deleted
        keywordClient.deleteKeyword(keywordId).get();
        assertThat(keywordClient.getKeyword(keywordId).get()).isNull();
    }

    @Test
    public void validation() {
        final Keyword keywordWithNulls = new Keyword();

        assertThat(catchThrowableOfType(() -> keywordClient.postKeyword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyword may not be null");

        assertThat(catchThrowableOfType(() -> keywordClient.postKeyword(keywordWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyword.shortNumberId may not be null", "keyword.keywordText may not be null",
                        "keyword.mode may not be null", "keyword.forwardUrl may not be null", "keyword.enabled may not be null");

        assertThat(catchThrowableOfType(() -> keywordClient.getKeyword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keywordId may not be null");

        assertThat(catchThrowableOfType(() -> keywordClient.getKeyword(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keywordId may not be null");

        assertThat(catchThrowableOfType(() -> keywordClient.putKeyword(keywordWithNulls), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keyword.shortNumberId may not be null", "keyword.keywordId may not be null",
                        "keyword.keywordText may not be null", "keyword.mode may not be null", "keyword.forwardUrl may not be null",
                        "keyword.enabled may not be null");

        assertThat(catchThrowableOfType(() -> keywordClient.deleteKeyword(null), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keywordId may not be null");

        assertThat(catchThrowableOfType(() -> keywordClient.deleteKeyword(""), InvalidInputException.class).getViolations())
                .containsExactlyInAnyOrder("keywordId may not be null");
    }
}
