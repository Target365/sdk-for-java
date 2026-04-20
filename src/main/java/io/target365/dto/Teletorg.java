package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.ZonedDateTime;

/**
 * Teletorg.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Teletorg {
    /**
     * The called teletorg number.
     */
    private String bNumber;

    /**
     * Start time of the charged teletorg voice call.
     */
    private ZonedDateTime startTime;

    /**
     * Charged voice call duration, in seconds.
    */
    private int duration;

    /**
     * Start price for the charged voice call. Start price should be in 1/100 NOK (e.g. 1 NOK = 100 Øre)
    */
    private Double startPrice;
}
