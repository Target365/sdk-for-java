package io.target365.handler;

import io.target365.exception.InvalidResponseException;
import okhttp3.Response;

import java.util.List;

public interface ResponseHandler {

    /**
     * Handles response and passes it through if codes are valid, otherwise throws an {@link InvalidResponseException}
     *
     * @param response response
     * @param codes    codes which should be considered valid
     * @return response
     * @throws InvalidResponseException exception
     */
    Response handle(final Response response, final List<Integer> codes) throws InvalidResponseException;

}
