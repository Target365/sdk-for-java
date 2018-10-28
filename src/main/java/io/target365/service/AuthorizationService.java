package io.target365.service;

public interface AuthorizationService {

    String signHeader(
        final Signer signer, final String key, final String method,
        final String uri, final String content
    );

    Boolean verifyHeader(
        final Verifier verifier, final String method, final String uri, final long timestamp,
        final String nonce, final String content, final String sign
    );

}
