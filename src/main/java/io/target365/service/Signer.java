package io.target365.service;

public interface Signer {

    /**
     * Signs message and returns signature string
     *
     * @param message Message to sign
     * @return Signature string
     */
    String sign(final String message);

}
