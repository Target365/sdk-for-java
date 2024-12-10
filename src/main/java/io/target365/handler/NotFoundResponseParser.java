package io.target365.handler;

import lombok.AllArgsConstructor;
import okhttp3.Response;

@AllArgsConstructor
public class NotFoundResponseParser implements ResponseParser {

    public String parse(final Response response) {
        response.close();
        return null;
    }

}
