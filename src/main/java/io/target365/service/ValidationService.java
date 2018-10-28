package io.target365.service;

import java.util.List;

public interface ValidationService {

    /**
     * Performs validation. If at least one validator is not valid throws exception
     *
     * @param validators List of validators
     */
    void validate(final Validator... validators);

    interface Validator {
        List<String> valid();
    }
}
