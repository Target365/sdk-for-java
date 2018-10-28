package io.target365.client;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.concurrent.Future;

public interface VerificationClient {

    String X_ECDSA_SIGNATURE_PATTERN = "^[A-Za-z0-9_-]+:[0-9]+:[A-Za-z0-9_-]+:[A-Za-z0-9_+/=]+$";

    Future<Boolean> verifySignature(
        @NotBlank final String method, @NotBlank final String uri, @NotNull final String content,
        @NotBlank @Pattern(regexp = X_ECDSA_SIGNATURE_PATTERN) final String xEcdsaSignatureString
    );
}
