package io.target365.client;

import javax.validation.constraints.NotBlank;
import java.util.concurrent.Future;

public interface ReversePaymentClient {
    /**
     * Reverses a payment transaction (asynchronously). This method is idempotent and can be called multiple times without problems.
     *
     * @param transactionId Transaction id.
     * @return resulting reverse transaction id.
     */
    Future<String> reversePayment(@NotBlank final String transactionId);
}
