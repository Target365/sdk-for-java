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
    @NotBlank
    private String transactionId;

    /**
     * Merchant id.
     */
    @NotBlank
    private String merchantId;

    /**
     * Short number.
     */
    @NotBlank
    private String shortNumber;

    /**
     * Recipient phone number.
     */
    private String recipient;

    /**
     * Price.
     */
    @NotNull
    private Double price;

    /**
     * Service code.
     */
    @NotBlank
    private String serviceCode;

    /**
     * Invoice text.
     */
    @NotBlank
    private String invoiceText;

    /**
     * Status code.
     * See {@link io.target365.dto.enums.StatusCode} for possible values
     */
    private String statusCode;

    /**
     * Session id. Can be used as the clients to get all out-messages associated to a specific session.
     */
    private String sessionId;

    /**
     * Correlation id. Can be used as the clients correlation id for tracking messages and delivery reports.
     */
    private String correlationId;

    /**
     * One-Time-Password. Used with previously sent one-time-passwords.
     */
    private String oneTimePassword;

    /**
     * Tags associated with transaction. Can be used for statistics and grouping.
     */
    private List<String> tags;

    /**
     * Associated custom properties.
     */
    private Map<String, Object> properties;

    /**
     * Read-only: Whether billing has been performed. Null means unknown status.
     */
    private Boolean billed;

    /**
     * Created time. Read-only property.
     */
    private ZonedDateTime created;

    /**
     * Last modified time. Read-only property.
     */
    private ZonedDateTime lastModified;

}
