package io.target365.dto.enums;

/**
 * Status code
 */
public enum StatusCode {

    /**
     * Message is queued
     */
    Queued,

    /**
     * Message has been sent
     */
    Sent,

    /**
     * Message has failed
     */
    Failed,

    /**
     * Message has been delivered/billed
     */
    Ok,

    /**
     * Message billing has been reversed
     */
    Reversed
}