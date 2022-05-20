package io.target365.dto.enums;

 /**
 * Detailed status code
 */
public enum DetailedStatusCode {
    
    /**
    * Message has no status.
    */
    None,

    /**
    * Message is delivered to destination.
    */
    Delivered,

    /**
    * Message validity period has expired.
    */
    Expired,

    /**
    * Message is undeliverable.
    */
    Undelivered,

    /**
    * Message is in invalid state.
    */
    UnknownError,

     /**
      * Message is in invalid state.
      */
     OtherError,

     /**
    * Message is in a rejected state.
    */
    Rejected,

    /**
    * Unknown subscriber.
    */
    UnknownSubscriber,

    /**
    * Subscriber unavailable.
    */
    SubscriberUnavailable,

    /**
    * Subscriber barred.
    */
    SubscriberBarred,

    /**
    * Insufficient funds.
    */
    InsufficientFunds,

    /**
    * Registration required.
    */
    RegistrationRequired,

    /**
    * Unknown age.
    */
    UnknownAge,

    /**
    * Duplicate transaction.
    */
    DuplicateTransaction,

    /**
    * Subscriber limit exceeded.
    */
    SubscriberLimitExceeded,

    /**
    * Max pin retry reached.
    */
    MaxPinRetry,

    /**
    * Invalid amount.
    */
    InvalidAmount,

    /**
    * One-time password expired.
    */
    OneTimePasswordExpired,

    /**
    * One-time password failed.
    */
    OneTimePasswordFailed,

    /**
    * Subscriber too young.
    */
    SubscriberTooYoung,

    /**
    * Timeout error.
    */
    TimeoutError,

     /**
     * Transaction is pending.
     */
     Pending,

     /**
     * Temporary error.
     */
     TemporaryError,

     /**
     * Missing pre-auth.
     */
     MissingPreAuth,

     /**
     * Missing delivery report.
     */
     MissingDeliveryReport,

     /**
     * Agent has Transaction.
     */
     UserInTransaction,

     /**
     * Connection offline.
     */
     ConnectionOffline,

     /**
     * Invalid username/password.
     */
     InvalidCredentials,

     /**
     * Invalid OTP transaction.
     */
     InvalidOtp,

     /**
     * Technical error returned from Card Payment PSP.
     */
     CardPspError,

     /**
     * Technical error MNO.
     */
     MnoError,

     /**
     * Message was stopped because of monthly limit or similar.
     */
     Stopped,
}