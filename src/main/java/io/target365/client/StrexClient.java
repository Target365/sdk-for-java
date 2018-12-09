package io.target365.client;

import io.target365.dto.StrexMerchantId;
import io.target365.dto.StrexOneTimePassword;
import io.target365.dto.StrexTransaction;

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
     * Creates a new one-time password.
     *
     * @param oneTimePassword Strex one-time password.
     * @return Void
     */
    Future<Void> postStrexOneTimePassword(@NotNull @Valid final StrexOneTimePassword oneTimePassword);

    /**
     * Gets a strex one-time password.
     *
     * @param transactionId Transaction id.
     * @return A strex one-time password.
     */
    Future<StrexOneTimePassword> getStrexOneTimePassword(@NotBlank final String transactionId);

    /**
     * Creates a new strex transaction.
     *
     * @param transaction Strex transaction.
     * @return Void
     */
    Future<Void> postStrexTransaction(@NotNull @Valid final StrexTransaction transaction);

    /**
     * Gets a strex transaction.
     *
     * @param transactionId Transaction id.
     * @return A strex transaction.
     */
    Future<StrexTransaction> getStrexTransaction(@NotBlank final String transactionId);

    /**
     * Reverses a previous strex transaction.
     *
     * @param transactionId Transaction id.
     * @return Resource uri of reversed transaction.
     */
    Future<String> reverseStrexTransaction(@NotBlank final String transactionId);

}
