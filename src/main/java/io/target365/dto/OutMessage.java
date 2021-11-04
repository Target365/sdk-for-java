package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.target365.dto.enums.DeliveryMode;
import io.target365.dto.enums.Priority;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

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
     * Gets the number of sms message parts are required for a given text and encoding
     */
    public static int getSmsPartsForText(String text, boolean unicode)
    {
        if (unicode) {
            return (text.length() <= 70) ? 1 : (int)Math.ceil(text.length() / 67.0);
        }

        final char[] extendedChars = new char[] { '\f', '^', '{', '}', '\\', '[', '~', ']', '|', '€' };
        HashSet<Character> extendedCharSet = new HashSet<Character>();

        for (char c : extendedChars) {
            extendedCharSet.add((c));
        }

        int totalCharCount = 0;

        for (char c : text.toCharArray()) {
            totalCharCount++;

            if (extendedCharSet.contains(c)) {
                totalCharCount++;
            }
        }

        if (totalCharCount <= 160) {
            return 1;
        }

        final int maxSeptetsPerPart = 153;
        int parts = 1;
        int septets = 0;

        for (char c : text.toCharArray())
        {
            if (septets == maxSeptetsPerPart || (septets == (maxSeptetsPerPart - 1) && extendedCharSet.contains(c)))
            {
                parts++;
                septets = 0;
            }

            if (extendedCharSet.contains(c)) {
                septets += 2;
            } else {
                septets += 1;
            }
        }

        return parts;
    }

    /**
     * Transaction id. Must be unique per message if used. This can be used for guarding against resending messages.
     */
    private String transactionId;

    /**
     * Session id. This can be used as the clients to get all out-messages associated to a specific session.
     */
    private String sessionId;

    /**
     * Correlation id. This can be used as the clients' correlation id for tracking messages and delivery reports.
     */
    private String correlationId;

    /**
     * Keyword id associated with message. Can be null.
     */
    private String keywordId;

    /**
     * Sender. Can be an alphanumeric string, a phone number or a short number.
     */
    @NotNull
    private String sender;

    /**
     * Recipient phone number.
     */
    @NotNull
    private String recipient;

    /**
     * Content. The actual text message content.
     */
    @NotNull
    private String content;

    /**
     * Strex data
     */
    @Valid
    private StrexData strex;

    /**
     * Send time, in UTC. If omitted the send time is set to ASAP.
     */
    private ZonedDateTime sendTime;

    /**
     * Message Time-To-Live (TTL) in minutes. Must be between 5 and 1440. Default value is 120.
     */
    @Min(value = 5)
    @Max(value = 1440)
    private Integer timeToLive = 120;

    /**
     * Priority. Can be 'Low', 'Normal' or 'High'. Default value is Normal.
     * See {@link io.target365.dto.enums.Priority} for possible values
     */
    private String priority = Priority.Normal.name();

    /**
     * Message delivery mode. Can be either 'AtLeastOnce' or 'AtMostOnce'. Default value is AtMostOnce.
     * See {@link io.target365.dto.enums.DeliveryMode} for possible values
     */
    private String deliveryMode = DeliveryMode.AtMostOnce.name();

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
     * See {@link io.target365.dto.enums.StatusCode} for possible values
     */
    private String statusCode;

    /**
     * Delivery status code. Read-only property.
     * See {@link io.target365.dto.enums.DetailedStatusCode} for possible values
     */
    private String detailedStatusCode;

    /**
     * Set to true to allow unicode SMS, false to fail if content is unicode, null to replace unicode chars to '?'.
     */
    private Boolean allowUnicode;

    /**
     * Whether message was delivered. Null if status is unknown. Read-only property.
     */
    private Boolean delivered;

    /**
     *  Operator id (from delivery report).
     */
    private Boolean operatorId;

    /**
     * External SMSC transaction id.
     */
    private String smscTransactionId;

    /**
     * SMSC message parts.
     */
    private Integer smscMessageParts;

    /**
     * Tags associated with message. Can be used for statistics and grouping.
     */
    private List<String> tags;

    /**
     * Associated custom properties.
     */
    private Map<String, Object> properties;
}
