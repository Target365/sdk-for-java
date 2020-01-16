package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Strex data.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrexData {

    private static final long serialVersionUID = -4215212257234662326L;

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
     * Invoice text.
     */
    @NotNull
    private String invoiceText;

    /**
     * Price.
     */
    @NotNull
    private Double price;

    /**
     * Read-only: Whether billing has been performed. Null means unknown status.
     */
    private Boolean billed;

}
