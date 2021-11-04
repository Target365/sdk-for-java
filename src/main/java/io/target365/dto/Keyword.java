package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * Keyword.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Keyword implements Serializable {

    private static final long serialVersionUID = -1571238197416612533L;

    /**
     * Keyword id returned by Target365.
     */
    private String keywordId;

    /**
     * Short number associated with keyword.
     */
    @NotNull
    private String shortNumberId;

    /**
     * Keyword text.
     */
    @NotNull
    private String keywordText;

    /**
     * Keyword mode. Can be 'Text', 'Wildcard' or 'Regex'.
     */
    @NotNull
    private Mode mode;

    /**
     * Keyword forward url to post incoming messages.
     */
    @NotNull
    private String forwardUrl;

    /**
     * Whether keyword is enabled.
     */
    @NotNull
    private Boolean enabled;

    /**
     * Creation date. Read-only property.
     */
    private ZonedDateTime created;

    /**
     * Last modified date. Read-only property.
     */
    private ZonedDateTime lastModified;

    /**
     * Custom properties associated with keyword. Will be propagated to incoming messages.
     */
    private Map<String, Object> customProperties;

    /**
     * Tags associated with keyword. Can be used for statistics and grouping.
     */
    private List<String> tags;

    /**
    * Alias keywords associated with keyword.
    */
    private List<String> aliases;

    /**
     * Keyword mode
     */
    public enum Mode {
        Text, Wildcard, Regex
    }
}
