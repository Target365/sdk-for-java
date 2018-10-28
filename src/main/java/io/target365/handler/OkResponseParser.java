package io.target365.handler;

import io.target365.util.Util;
import lombok.AllArgsConstructor;
import okhttp3.Response;

import java.util.Optional;

@AllArgsConstructor
public class OkResponseParser implements ResponseParser {

    public String parse(final Response response) {
        return Optional.of(response).map(r -> Util.wrap(r::body)).map(b -> Util.wrap(b::string)).orElse("");
    }

}
