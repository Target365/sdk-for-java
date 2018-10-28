package io.target365.handler;

import okhttp3.Response;

public interface ResponseParser {

    /**
     * Parses the response
     *
     * @param response response
     * @return parsing result
     */
    String parse(final Response response);

}
