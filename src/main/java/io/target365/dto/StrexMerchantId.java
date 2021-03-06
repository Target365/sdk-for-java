package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Strex merchant id.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrexMerchantId implements Serializable {

    private static final long serialVersionUID = 5571228597516312539L;

    /**
     * Strex merchant id.
     */
    @NotNull
    private String merchantId;

    /**
     * Short number id.
     */
    private String[] shortNumberIds;

    /**
     * This is a write-only property and will always return null.
     */
    private String password;

}
