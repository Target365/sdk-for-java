package io.target365.service;

public interface Verifier {

    /**
     * Verifies message signature
     *
     * @param message Message to verify
     * @param sign    Signature
     * @return true or false
     */
    boolean verify(final String message, final String sign);

}
