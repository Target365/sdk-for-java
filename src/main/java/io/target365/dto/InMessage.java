package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * In-message.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class InMessage implements Serializable {

    private static final long serialVersionUID = 7313232547611242511L;

    /**
     * Transaction id. Must be unique per message if used. This can be used for guarding against resending messages.
     */
    private String transactionId;

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
     * Whether in-message is a stop subscription message.
     */
    private Boolean isStopMessage;

    /**
     * Created time. Read-only property.
     */
    private ZonedDateTime created;

    /**
     * Tags associated with message. Can be used for statistics and grouping.
     */
    private List<String> tags;

    /**
     * Associated custom properties.
     */
    private Map<String, Object> properties;

}
