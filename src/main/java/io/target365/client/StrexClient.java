package io.target365.client;

import io.target365.dto.*;
import io.target365.dto.enums.UserValidity;

import javax.annotation.Nullable;
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
    Future<StrexMerchantId> getMerchantId(@NotNull final String merchantId);

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
    Future<StrexOneTimePassword> getStrexOneTimePassword(@NotNull final String transactionId);

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
    Future<StrexTransaction> getStrexTransaction(@NotNull final String transactionId);

    /**
     * Reverses a previous strex transaction.
     *
     * @param transactionId Transaction id.
     * @return Resource uri of reversed transaction.
     */
    Future<String> reverseStrexTransaction(@NotNull final String transactionId);

    /**
     * Gets strex user validity.
     *
     * @param transactionId Transaction id.
     * @param merchantId Merchant id.
     * @return A strex transaction.
     */
    Future<UserValidity> getStrexUserValidity(@NotNull final String transactionId, @Nullable final String merchantId);

    /**
     * Saves or updates a one-click config.
     *
     * @param config One-click config.
     * @return Void
     */
    Future<Void> saveOneClickConfig(@NotNull @Valid final OneClickConfig config);

    /**
     * Gets a one-click config.
     *
     * @param configId One-click config id.
     * @return One-click config.
     */
    Future<OneClickConfig> getOneClickConfig(@NotNull final String configId);

    /**
     * Initiates Strex-registation by SMS.
     *
     * @param registrationSms Strex registration sms.
     * @return Void
     */
    Future<Void> sendStrexRegistrationSms(@NotNull final StrexRegistrationSms registrationSms);
}
