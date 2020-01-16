package io.target365.client;

import io.target365.dto.InMessage;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;

public interface InMessageClient {

    /**
     * Gets an in-message.
     *
     * @param shortNumberId Short number id.
     * @param transactionId Message transaction id.
     * @return An in-message.
     */
    Future<InMessage> getInMessage(@NotNull final String shortNumberId, @NotNull final String transactionId);

}
