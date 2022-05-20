package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Strex data.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrexData {

    private static final long serialVersionUID = -4215212257234662326L;

    /**
     * Merchant id - provided by Strex.
     */
    @NotNull
    private String merchantId;

    /**
     * Service code - provided by Strex.
     */
    @NotNull
    private String serviceCode;

    /**
     * Business model - optional and provided by Strex.
     */
    private String businessModel;

    /**
     * Service id used for pre-authorizations and recurring billing.
     */
    private String preAuthServiceId;

    /**
     * Age requirements - typically 18 for subscriptions and adult content. Default value is 0.
     */
    private int age;

    /**
     * Whether the transaction should be flagged as restricted - provided by Strex.
     */
    private boolean isRestricted;

    /**
     * Whether to use sms confirmation.
     */
    private Boolean smsConfirmation;

    /**
     * Invoice text - this shows up on the Strex portal for end users.
     */
    @NotNull
    private String invoiceText;

    /**
     * Price - price to charge in whole NOK. Two decimals are supported (øre).
     */
    @NotNull
    private Double price;

    /**
     * Timeout in minutes for transactions which trigger end user registration. Default value is 5.
     */
    private Integer timeout = new Integer(5);

    /**
     * Read-only: Whether billing has been performed. Null means unknown status.
     */
    private Boolean billed;

    /**
     * Read-only: Strex payment gateway result code.
     */
    private String resultCode;

    /**
     * Read-only: Strex payment gateway result description.
     */
    private String resultDescription;
}
