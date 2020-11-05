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
 * Strex registration SMS.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrexRegistrationSms implements Serializable {

    private static final long serialVersionUID = 1166376700460775495L;

    /**
     * Merchant Id.
     */
    @NotNull
    private String merchantId;

    /**
     * Transaction Id.
     */
    @NotNull
    private String transactionId;

    /**
     * Recipient mobile number.
     */
    @NotNull
    private String recipient;

    /**
     * SMS text to be added in the registration SMS. Registration URL will be added by Strex.
     */
    private String smsText;
}
