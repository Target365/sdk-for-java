package io.target365.client;

import io.target365.dto.LookupResult;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;

public interface LookupClient {
    /**
     * Looks up address info on a mobile phone number.
     *
     * @param msisdn Phone number in international format with a leading plus e.g. '+4798079008'.
     * @return LookupResult object.
     */
    Future<LookupResult> addressLookup(@NotNull final String msisdn);

    /**
     * Looks up address info from free text (name, address...).
     *
     * @param freetext Free text like name or address.
     * @return Array of LookupResult objects.
     */
    Future<LookupResult[]> freetextLookup(@NotNull final String freetext);
}
