package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.target365.dto.enums.DeliveryMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Strex transaction.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrexTransaction implements Serializable {

    private static final long serialVersionUID = -1511653556213312531L;

    /**
     * Transaction id. Must be unique per message if used. Can be used for guarding against resending messages.
     */
    @NotNull
    private String transactionId;

    /**
     * Session id. Can be used as the clients to get all out-messages associated to a specific session.
     */
    private String sessionId;

    /**
     * Correlation id. Can be used as the clients correlation id for tracking messages and delivery reports.
     */
    private String correlationId;

    /**
     * Keyword id associated with transaction. Can be null.
     */
    private String keywordId;

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
    private Integer timeout = 5;

    /**
     * Service id used for pre-authorizations and recurring billing.
     */
    private String preAuthServiceId;

    /**
     * Service description used for pre-authorizations and recurring billing.
     */
    private String preAuthServiceDescription;

    /**
     * Short number.
     */
    @NotNull
    private String shortNumber;

    /**
     * Recipient phone number.
     */
    private String recipient;

    /**
     * Optional SMS text message content (Not used for direct billing).
     */
    private String content;

    /**
     * One-Time-Password. Used with previously sent one-time-passwords.
     */
    private String oneTimePassword;

    /**
     * Message delivery mode. Can be either 'AtLeastOnce' or 'AtMostOnce'. Default value is AtMostOnce.
     * See {@link io.target365.dto.enums.DeliveryMode} for possible values
     */
    private String deliveryMode = DeliveryMode.AtMostOnce.name();

    /**
     * Tags associated with transaction. Can be used for statistics and grouping.
     */
    private List<String> tags;

    /**
     * Associated custom properties.
     */
    private Map<String, Object> properties;

    /**
     * Created time. Read-only property.
     */
    private ZonedDateTime created;

    /**
     * Last modified time. Read-only property.
     */
    private ZonedDateTime lastModified;

    /**
     * Status code.
     * See {@link io.target365.dto.enums.StatusCode} for possible values.
     */
    private String statusCode;

    /**
     * Detailed status code.
     * See {@link io.target365.dto.enums.DetailedStatusCode} for possible values.
     */
    private String detailedStatusCode;

    /**
     * Status description. Read-only property.
     */
    private String statusDescription;

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
