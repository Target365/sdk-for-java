package io.target365.handler;

import io.target365.exception.InvalidResponseException;
import io.target365.util.Util;
import lombok.AllArgsConstructor;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class InvalidResponseHandler implements ResponseHandler {

    @Override
    public Response handle(final Response response, final List<Integer> codes) throws InvalidResponseException {
        if (!codes.contains(response.code())) {
            try {
                throw new InvalidResponseException(response.code(), response.message(),
                        Optional.ofNullable(response.body()).map(rb -> Util.suppress(rb::string)).orElse(""));
            } finally {
                response.close();
            }
        }

        return response;
    }

}
