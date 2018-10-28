package io.target365.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class InvalidResponseException extends Exception {

    private final Integer code;
    private final String message;
    private final String body;

}
