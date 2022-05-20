package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Lookup result.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class LookupResult implements Serializable {

    private static final long serialVersionUID = -4571212597415316539L;

    /**
     * Creation date. Read-only property.
     */
    private ZonedDateTime created;

    /**
     * MSISDN (Mobile phone number). Includes a leading pluss and country-code such as '+4798079008'.
     */
    private String msisdn;

    /**
     * Landline phone number. Includes a leading pluss and country-code such as '+4722980790'.
     */
    private String landline;

    /**
     * First name.
     */
    private String firstName;

    /**
     * Middle name.
     */
    private String middleName;

    /**
     * Last name.
     */
    private String lastName;

    /**
     * Company name.
     */
    private String companyName;

    /**
     * Company organization number.
     */
    private String companyOrgNo;

    /**
     * Street name.
     */
    private String streetName;

    /**
     * Street number.
     */
    private String streetNumber;

    /**
     * Street letter.
     */
    private String streetLetter;

    /**
     * Zip code.
     */
    private String zipCode;

    /**
     * City.
     */
    private String city;

    /**
     * Gender. Can be 'Male', 'Female' or 'Unknown'.
     */
    private Gender gender;

    /**
    * Date of birth, in format 'yyyy-dd-MM'.
    */
    private String dateOfBirth;

    /**
     * Age. Can be null for 'Unknown'.
     */
    private Integer age;

    /**
     * Deceased date. Null if not deceased. Format is 'yyyy-dd-MM'.
     */
    private String deceasedDate;

    /**
     * Gender
     */
    @Getter
    public enum Gender {
        M("Male"), F("Female"), U("Unknown");

        private String value;

        Gender(final String value) {
            this.value = value;
        }
    }
}
