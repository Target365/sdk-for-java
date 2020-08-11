package io.target365.dto.enums;

/**
 * Strex user validity
 */
public enum UserValidity {

    /**
     * Not registered (0)
     */
    Unregistered,

    /**
     * Registered, but not valid for licensed purchase (1)
     */
    Partial,

    /**
     * Registered and valid for licensed purchase (2)
     */
    Full,

    /**
     * Barred (3)
     */
    Barred
}