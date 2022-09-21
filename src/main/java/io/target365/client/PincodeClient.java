package io.target365.client;

import io.target365.dto.*;
import io.target365.dto.enums.UserValidity;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.Future;

public interface PincodeClient {

    /**
     * Creates a pin code and sends it as a sms message.
     *
     * @param pincode Pincode object.
     * @return Void
     */
    Future<Void> postPincode(@NotNull @Valid final Pincode pincode);

    /**
     * Verifies a pin code.
     *
     * @param transactionId Transaction id.
     * @param pincode Pin code to verify.
     * @return If pin code is correct (true/false).
     */
    Future<Boolean> getPincodeVerification(@NotNull final String transactionId, @NotNull final String pincode);
}
