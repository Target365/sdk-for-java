package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Strex one-time password.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrexOneTimePassword implements Serializable {

    private static final long serialVersionUID = -3889243124621221411L;

    /**
     * Transaction id.
     */
    @NotNull
    private String transactionId;

    /**
     * Merchant id.
     */
    @NotNull
    private String merchantId;

    /**
     * Recipient phone number.
     */
    @NotNull
    private String recipient;

    /**
     * SMS Sender (originator).
     */
    private String sender;

    /**
     * Whether one-time password is for recurring payment.
     */
    @NotNull
    private Boolean recurring;

    /**
     * Text string which will be prepended to the standard Strex SMS message sent to the subscriber.
     */
    private String messagePrefix;

    /**
     * Text string which will be appended to the standard Strex SMS message sent to the subscriber.
     */
    private String messageSuffix;

    /**
     * Deprecated, use MessagePrefix and MessageSuffix instead.
     */
    private String message;

    /**
     * Whether one-time password sms has been delivered. Null means unknown.
     */
    private Boolean delivered;
}
