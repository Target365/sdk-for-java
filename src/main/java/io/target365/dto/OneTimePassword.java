package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
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
public class OneTimePassword implements Serializable {

    private static final long serialVersionUID = -38411;

    /**
     * Transaction id.
     */
    @NotBlank
    private String transactionId;

    /**
     * Strex merchant id.
     */
    @NotBlank
    private String merchantId;

    /**
     * Recipient phone number.
     */
    @NotBlank
    private String recipient;

    /**
     * Whether one-time password is for recurring payment.
     */
    private boolean recurring;

    /**
     * Short number id.
     */
    private String sender;

    /**
     * One-time password message. This is prefixed to the generated password message.
     */
    private String message;

    /**
     * Whether one-time password sms has been delivered. Null means unknown.
     */
    private Boolean delivered;
}
