package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Public key.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicKey {

    private static final long serialVersionUID = 1245211231672131213L;

    /**
     * Public key name
     */
    @NotNull
    private String name;

    /**
     * Public key in DER(ANS.1) base64 format.
     */
    @NotNull
    private String publicKeyString;

    /**
     * Signing algorithm used. Usually 'ECDsaP256'.
     */
    @NotNull
    private String signAlgo;

    /**
     * Hashing algorithm. Usually 'SHA256'.
     */
    private String hashAlgo;

    /**
     * Created time.
     */
    private ZonedDateTime created;

    /**
     * Last modified time.
     */
    private ZonedDateTime lastModified;

    /**
     * Not-usable-before time.
     */
    private ZonedDateTime notUsableBefore;

    /**
     * Expiry time.
     */
    private ZonedDateTime expiry;

}
