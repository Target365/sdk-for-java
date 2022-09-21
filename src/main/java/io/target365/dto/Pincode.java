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
 * Pin code.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pincode implements Serializable {

    private static final long serialVersionUID = -3889243124621221411L;

    /**
     * Transaction id.
     */
    @NotNull
    private String transactionId;

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
     * Text string which will be prepended to the standard Strex SMS message sent to the subscriber.
     */
    private String prefixText;

    /**
     * Text string which will be appended to the standard Strex SMS message sent to the subscriber.
     */
    private String suffixText;
}
