package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Out-message.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutMessage implements Serializable {

    private static final long serialVersionUID = 2311238557213312531L;

    /**
     * Transaction id. Must be unique per message if used. This can be used for guarding against resending messages.
     */
    private String transactionId;

    /**
     * Correlation id. This can be used as the clients correlation id for tracking messages and delivery reports.
     */
    private String correlationId;

    /**
     * Keyword id associated with message. Can be null.
     */
    private String keywordId;

    /**
     * Sender. Can be an alphanumeric string, a phone number or a short number.
     */
    @NotBlank
    private String sender;

    /**
     * Recipient phone number.
     */
    @NotBlank
    private String recipient;

    /**
     * Content. The actual text message content.
     */
    @NotBlank
    private String content;

    /**
     * Strex data
     */
    private StrexData strex;

    /**
     * Send time, in UTC. If omitted the send time is set to ASAP.
     */
    private ZonedDateTime sendTime;

    /**
     * Message Time-To-Live (TTL) in minutes. Must be between 5 and 1440. Default value is 120.
     */
    @Range(min = 5, max = 1440) // TODO Add validation here
    private Integer timeToLive = 120;

    /**
     * Priority. Can be 'Low', 'Normal' or 'High'. Default value is Normal.
     */
    private Priority priority = Priority.Normal;

    /**
     * Message delivery mode. Can be either AtLeastOnce or AtMostOnce. Default value is AtMostOnce.
     */
    private DeliveryMode deliveryMode = DeliveryMode.AtMostOnce;

    /**
     * Delivery report url.
     */
    private String deliveryReportUrl;

    /**
     * Last modified time. Read-only property.
     */
    private ZonedDateTime lastModified;

    /**
     * Created time. Read-only property.
     */
    private ZonedDateTime created;

    /**
     * Delivery status code. Read-only property.
     */
    private StatusCode statusCode;

    /**
     * Whether message was delivered. Null if status is unknown. Read-only property.
     */
    private Boolean delivered;

    /**
     * Tags associated with message. Can be used for statistics and grouping.
     */
    private List<String> tags;

    /**
     * Associated custom properties.
     */
    private Map<String, Object> properties;

    /**
     * Priority
     */
    public enum Priority {
        Low, Normal, High
    }

    /**
     * Delivery mode
     */
    public enum DeliveryMode {
        AtLeastOnce, AtMostOnce
    }

    /**
     * Status code
     */
    public enum StatusCode {

        /**
         * Message is queued
         */
        Queued,

        /**
         * Message has been sent
         */
        Sent,

        /**
         * Message has failed
         */
        Failed,

        /**
         * Message has been delivered/billed
         */
        Ok,

        /**
         * Message billing has been reversed
         */
        Reversed
    }
}
