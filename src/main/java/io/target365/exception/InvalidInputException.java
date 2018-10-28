package io.target365.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class InvalidInputException extends RuntimeException {

    private final List<String> violations;

}
