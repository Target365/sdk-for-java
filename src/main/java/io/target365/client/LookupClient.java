package io.target365.client;

import io.target365.dto.LookupResult;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;

public interface LookupClient {
    /**
     * Lookup a phone number.
     *
     * @param msisdn Phone number in international format with a leading plus e.g. '+4798079008'.
     * @return Lookup result.
     */
    Future<LookupResult> addressLookup(@NotNull final String msisdn);
}
