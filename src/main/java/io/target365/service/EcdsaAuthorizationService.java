package io.target365.service;

import io.target365.util.Util;
import lombok.AllArgsConstructor;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class EcdsaAuthorizationService implements AuthorizationService {

    private static final String HMAC = "HMAC";
    private static final String DIGEST_ALGORITHM = "SHA-256";

    @Override
    public String signHeader(
        final Signer signer, final String key, final String method,
        final String uri, final String content
    ) {
        final long timestamp = ZonedDateTime.now().toEpochSecond();
        final String nonce = UUID.randomUUID().toString();
        final String hash = Optional.ofNullable(content).filter(s -> !s.isEmpty())
            .map(s -> s.getBytes(StandardCharsets.UTF_8))
            .map(b -> Util.wrap(() -> MessageDigest.getInstance(DIGEST_ALGORITHM).digest(b)))
            .map(DatatypeConverter::printBase64Binary)
            .orElse("");

        final String message = method.toLowerCase() + uri.toLowerCase() + timestamp + nonce + hash;
        final String sign = signer.sign(message);

        return HMAC + " " + key + ":" + timestamp + ":" + nonce + ":" + sign;
    }

    @Override
    public Boolean verifyHeader(
        final Verifier verifier, final String method, final String uri, final long timestamp,
        final String nonce, final String content, final String sign
    ) {
        final String hash = Optional.ofNullable(content).filter(s -> !s.isEmpty())
            .map(s -> s.getBytes(StandardCharsets.UTF_8))
            .map(b -> Util.wrap(() -> MessageDigest.getInstance(DIGEST_ALGORITHM).digest(b)))
            .map(DatatypeConverter::printBase64Binary)
            .orElse("");

        final String message = method.toLowerCase() + uri.toLowerCase() + timestamp + nonce + hash;

        return verifier.verify(message, sign);
    }

}
