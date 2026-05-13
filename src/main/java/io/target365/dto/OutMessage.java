package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.target365.dto.enums.DeliveryMode;
import io.target365.dto.enums.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;
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
    private static final Set<Character> gsm7Chars = new HashSet<>(Arrays.asList('@', '£', '$', '¥', 'è', 'é', 'ù', 'ì', 'ò', 'Ç', '\n', 'Ø', 'ø', '\r', 'Å', 'å', 'Δ', '_', 'Φ', 'Γ', 'Λ', 'Ω', 'Π', 'Ψ', 'Σ', 'Θ', 'Ξ', 'Æ', 'æ', 'ß', 'É', ' ', '!', '"', '#', '¤', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '¡', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ñ', 'Ü', '§', '¿', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ñ', 'ü', 'à'));
    private static final Set<Character> gsm7ExtendedChars = new HashSet<>(Arrays.asList('\f', '^', '{', '}', '\\', '[', '~', ']', '|', '€' ));

    /**
     * Gets the number of sms message parts are required for the given text.
     * @param text Text to evaluate
     * @return number of sms message parts
     */
    public static int getSmsPartsForText(String text)
    {
        return requiresUnicode(text) ? getUcs2SmsParts(text) : getGsm7SmsParts(text);
    }

    /**
     * Gets whether the given text requires Unicode (UCS2 SMS encoding).
     * @param text Text to evaluate
     * @return whether the text requires Unicode (UCS2 SMS encoding)
     */
    public static boolean requiresUnicode(String text)
    {
        if (text == null || text.isEmpty())
            return false;

        for (int i = 0; i < text.length(); i++)
        {
            if (!gsm7Chars.contains(text.charAt(i)) && !gsm7ExtendedChars.contains(text.charAt(i)))
                return true;
        }

        return false;
    }

    private static int getUcs2SmsParts(String text)
    {
        final int maxCharsPerPart = 67;

        if (text.length() <= 70)
            return 1;

        var count = 1;
        var chars = 0;

        for (var i = 0; i < text.length(); i++)
        {
            if (chars == maxCharsPerPart
                    || (chars == (maxCharsPerPart - 1) && text.charAt(i) >= '\ud800' && text.charAt(i) <= '\udbff'))
            {
                count++;
                chars = 0;
            }

            chars++;
        }

        return count;
    }

    private static int getGsm7SmsParts(String text)
    {
			final int maxBytesPerMessage = 140;
			final int maxSeptetsPerPart = 153;

        if (getGsm7CharByteCount(text) <= maxBytesPerMessage)
            return 1;

        var count = 1;
        var septets = 0;

        for (var i = 0; i < text.length(); i++)
        {
            if (septets == maxSeptetsPerPart || (septets == (maxSeptetsPerPart - 1) && gsm7ExtendedChars.contains(text.charAt(i))))
            {
                count++;
                septets = 0;
            }

            if (gsm7ExtendedChars.contains(text.charAt(i)))
                septets++;

            septets++;
        }

        return count;
    }

    private static int getGsm7CharByteCount(String text)
    {
        int septets = 0;

        for (var i = 0; i < text.length(); i++)
        {
            if (gsm7Chars.contains(text.charAt(i)))
            {
                septets++;
            }
            else
            {
                if (gsm7ExtendedChars.contains(text.charAt(i)))
                {
                    septets += 2;
                }
                else
                {
                    septets++;
                }
            }
        }

        var bytes = septets * 7 / 8;
        var remainder = septets * 7 % 8 > 0 ? 1 : 0;
        return bytes + remainder;
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
     * Status description. Read-only property.
     */
    private String statusDescription;

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
    private String operatorId;

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
