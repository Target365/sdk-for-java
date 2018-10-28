package io.target365.client;

import io.target365.dto.OutMessage;
import io.target365.dto.OutMessageBatch;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.Future;

public interface OutMessageClient {
    /**
     * Prepare MSISDNs for later sendings. This can greatly improve routing performance.
     *
     * @param msisdns MSISDNs to prepare as a string array.
     * @return Void
     */
    Future<Void> prepareMsisdns(@NotEmpty final List<@NotBlank String> msisdns);

    /**
     * Posts a new batch of up to 100 out-messages.
     *
     * @param outMessageBatch Out-message batch to post.
     * @return List of resource uri of created out-message.
     */
    Future<List<String>> postOutMessageBatch(@NotNull @Valid final OutMessageBatch outMessageBatch);

    /**
     * Posts a new out-message.
     *
     * @param outMessage Out-message to post.
     * @return Resource uri of created out-message.
     */
    Future<String> postOutMessage(@NotNull @Valid final OutMessage outMessage);

    /**
     * Gets an out-message.
     *
     * @param transactionId Message transaction id.
     * @return An out-message.
     */
    Future<OutMessage> getOutMessage(@NotBlank final String transactionId);

    /**
     * Updates a future scheduled out-message.
     *
     * @param outMessage    Text message to post.
     * @return Void
     */
    Future<Void> putOutMessage(@NotNull @Valid final OutMessage outMessage);

    /**
     * Deletes a future scheduled out-message.
     *
     * @param transactionId Message transaction id.
     * @return Void
     */
    Future<Void> deleteOutMessage(@NotBlank final String transactionId);
}
