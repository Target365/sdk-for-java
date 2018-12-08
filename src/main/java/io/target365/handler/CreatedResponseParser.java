package io.target365.handler;

import io.target365.client.Target365Client;
import io.target365.util.Util;
import lombok.AllArgsConstructor;
import okhttp3.Response;

import java.util.Optional;

@AllArgsConstructor
public class CreatedResponseParser implements ResponseParser {

    public String parse(final Response response) {
        return Optional.of(response).map(r -> r.header(Target365Client.Header.LOCATION))
                .map(l -> Util.getLast(l.split("/"))).orElse("");
    }

}
