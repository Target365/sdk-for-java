package io.target365.client;

import io.target365.dto.InMessage;

import javax.validation.constraints.NotBlank;
import java.util.concurrent.Future;

public interface InMessageClient {

    /**
     * Gets an in-message.
     *
     * @param shortNumberId Short number id.
     * @param transactionId Message transaction id.
     * @return An in-message.
     */
    Future<InMessage> getInMessage(@NotBlank final String shortNumberId, @NotBlank final String transactionId);

}
