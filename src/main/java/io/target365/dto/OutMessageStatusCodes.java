package io.target365.dto;

/**
 * Out-message status codes
 */
public class OutMessageStatusCodes
{
    /**
     * Queued - message is queued
     */
    public static final String Queued = "Queued";

    /**
     * Sent - message has been sent
     */
    public static final String Sent = "Sent";

    /**
     * Failed - message has failed
     */
    public static final String Failed = "Failed";

    /**
     * OK - message has been delivered/billed
     */
    public static final String Ok = "Ok";

    /**
     * Reversed - message billing has been reversed
     */
    public static final String Reversed = "Reversed";
}
