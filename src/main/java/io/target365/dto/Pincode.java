package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Pincode.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pincode implements Serializable {

    private static final long serialVersionUID = -1834528317146336723L;

    /**
     * TransactionId.
     */
    @NotNull
    private String transactionId;

    /**
     * Msisdn to receive pincode.
     */
    @NotNull
    private String recipient;

    /**
     * Sender of SMS.
     */
    @NotNull
    private String sender;

    /**
     * Text inserted before pincode (optional).
     */
    private String prefixText;

    /**
     * Text added after pincode (optional).
     */
    private String suffixText;

    /**
     * Length of pincode, 4-6 digits (optional).
     */
    private Integer pincodeLength;
}
