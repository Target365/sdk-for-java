package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * One-click config
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneClickConfig {

    private static final long serialVersionUID = -8710034405844932632L;

    /**
     * Unique config id.
     */
    @NotNull
    private String configId;

    /**
     * Created time.
     */
    private ZonedDateTime created;

    /**
     * Last modified time.
     */
    private ZonedDateTime lastModified;

    /**
     * Short number.
     */
    @NotNull
    private String shortNumber;

    /**
     * Merchant id.
     */
    @NotNull
    private String merchantId;

    /**
     * Service code.
     */
    @NotNull
    private String serviceCode;

    /**
     * Business model.
     */
    private String businessModel;

    /**
     * Age requirements - typically 18 for subscriptions and adult content.
     */
    private int age;

    /**
     * Whether the transaction should be flagged as restricted.
     */
    private boolean isRestricted;

    /**
     * Invoice text - Appears in the Strex portal available for end users.
     */
    @NotNull
    private String invoiceText;

    /**
     * Price - in whole NOK. Cents (øre) are supported by the first two decimal places.
     */
    @NotNull
    private Double price;

    /**
     * Timeout in minutes for transactions which trigger end user registration. Default value is 5.
     */
    private int timeout = 5;

    /**
     * Whether this config is for setting up subscriptions and recurring payments.
     */
    private boolean isRecurring;

    /**
     * One-click redirect url.
     */
    @NotNull
    private String redirectUrl;

    /**
     * One-click online text to use when oneclick msisdn detection is online and PIN-code can be skipped.
     */
    private String onlineText;

    /**
     * One-click text to use when oneclick msisdn detection is offline and PIN-code is used.
     */
    private String offlineText;

    /**
     * SubscriptionPrice - information to the user how much will be charged each interval (below).
     */
    private Double subscriptionPrice;

    /**
     * SubscriptionInterval - information to the user how often charging will happen. Possible values: weekly, monthly, yearly.
     */
    private String subscriptionInterval;

    /**
     * SubscriptionStartSms - sent when recurring transaction started.
     */
    private String subscriptionStartSms;
}
