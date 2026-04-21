package io.target365.client;

import io.target365.dto.Keyword;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.Future;

public interface KeywordClient {
    /**
     * List all keywords.
     *
     * @return Lists of all keywords.
     */
    Future<List<Keyword>> getKeywords();

    /**
     * Lists keywords.
     *
     * @param shortNumberId Filter for short number id (exact string match).
     * @param keywordText   Filter for keyword text (contains match).
     * @param mode          Filter for mode (exact string match).
     * @param tag           Filter for tag (exact string match).
     * @return Lists of all keywords.
     */
    Future<List<Keyword>> getKeywords(
            final String shortNumberId, final String keywordText,
            final Keyword.Mode mode, final String tag
    );

    /**
     * Posts a new keyword.
     *
     * @param keyword Keyword to post.
     * @return Resource uri of created keyword.
     */
    Future<String> postKeyword(@Valid final Keyword keyword);

    /**
     * Gets a keyword.
     *
     * @param keywordId Keyword id.
     * @return A keyword.
     */
    Future<Keyword> getKeyword(final String keywordId);

    /**
     * Updates a keyword.
     *
     * @param keyword Keyword.
     * @return Void
     */
    Future<Void> putKeyword(@Valid final Keyword keyword);

    /**
     * Deletes a keyword.
     *
     * @param keywordId Keyword id.
     * @return Void
     */
    Future<Void> deleteKeyword(final String keywordId);
}
