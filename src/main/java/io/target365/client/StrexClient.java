package io.target365.client;

import io.target365.dto.OneTimePassword;
import io.target365.dto.StrexMerchantId;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.Future;

public interface StrexClient {

    /**
     * Lists all strex merchant ids.
     *
     * @return Lists all registered strex merchant ids.
     */
    Future<List<StrexMerchantId>> getMerchantIds();

    /**
     * Gets a strex merchant id.
     *
     * @param merchantId Strex merchant id.
     * @return A strex merchant id.
     */
    Future<StrexMerchantId> getMerchantId(@NotBlank final String merchantId);

    /**
     * Updates or creates a new merchant id.
     *
     * @param merchantId      Strex merchant id.
     * @param strexMerchantId Merchant data.
     * @return Void
     */
    Future<Void> putMerchantId(@NotBlank final String merchantId, @NotNull @Valid final StrexMerchantId strexMerchantId);

    /**
     * Deletes a merchant id.
     *
     * @param merchantId Strex merchant id.
     * @return Void
     */
    Future<Void> deleteMerchantId(@NotBlank final String merchantId);

    /**
     * Updates or creates a new merchant id.
     *
     * @param oneTimePassword One-time password object.
     * @return Void
     */
    Future<Void> postOneTimePassword(@NotNull @Valid final OneTimePassword oneTimePassword);
}
