package io.target365.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.target365.exception.InvalidInputException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.validation.Validation;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
public class Jsr303ValidationService implements ValidationService {

    @Override
    public void validate(final Validator... validators) {
        final List<String> violations = (Arrays.stream(validators).map(Validator::valid)
            .flatMap(List::stream).collect(Collectors.toList()));

        if (!violations.isEmpty()) {
            throw new InvalidInputException(violations);
        }
    }

    /**
     * Checks that {@link Object} is not <code>null</code>
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NotNullValidator implements Validator {

        private final String field;
        private final Object object;

        @Override
        public List<String> valid() {
            return object == null ? ImmutableList.of(field + " must not be null") : ImmutableList.of();
        }

        public static Validator of(final String field, final Object value) {
            return new NotNullValidator(field, value);
        }
    }

    /**
     * Checks that {@link List} is not <code>null</code> and not empty
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NotEmptyValidator implements Validator {

        private final String field;
        private final List<?> list;

        @Override
        public List<String> valid() {
            return !NotNullValidator.of(field, list).valid().isEmpty() || list.isEmpty()
                ? ImmutableList.of(field + " must not be empty") : ImmutableList.of();
        }

        public static Validator of(final String field, final List<?> list) {
            return new NotEmptyValidator(field, list);
        }
    }

    /**
     * Checks that {@link String} is not <code>null</code> and not blank
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NotBlankValidator implements Validator {

        private final String field;
        private final String value;

        @Override
        public List<String> valid() {
            return !NotNullValidator.of(field, value).valid().isEmpty() || value.isEmpty()
                ? ImmutableList.of(field + " must not be blank") : ImmutableList.of();
        }

        public static Validator of(final String field, final String string) {
            return new NotBlankValidator(field, string);
        }
    }

    /**
     * Checks that {@link List} does not contain <code>null</code> or blank {@link String}'s
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NoBlanksValidator implements Validator {

        private final String field;
        private final List<String> list;

        @Override
        public List<String> valid() {
            return Optional.ofNullable(list).map(l -> IntStream.range(0, list.size())
                .mapToObj(i -> NotBlankValidator.of(field + ".[" + i + "]", list.get(i)).valid())
                .flatMap(List::stream).collect(Collectors.toList())).orElse(ImmutableList.of());
        }

        public static Validator of(final String field, final List<String> strings) {
            return new NoBlanksValidator(field, strings);
        }
    }

    /**
     * Checks that {@link String} is not <code>null</code> and conforms to the pattern
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PatternValidator implements Validator {

        private final String field;
        private final String value;
        private final String regexp;

        @Override
        public List<String> valid() {
            final Boolean result = Optional.ofNullable(value).map(v -> Pattern.compile(regexp).matcher(value).matches())
                .orElse(Boolean.FALSE);

            return NotBlankValidator.of(field, value).valid().isEmpty() && result
                ? ImmutableList.of() : ImmutableList.of(field + " must conform to the pattern " + regexp);
        }

        public static Validator of(final String field, final String value, final String regexp) {
            return new PatternValidator(field, value, regexp);
        }
    }

    /**
     * Checks that timestamp is in range specified
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class TimestampValidator implements Validator {

        private final String field;
        private final long value;
        private final long lower;
        private final long upper;

        @Override
        public List<String> valid() {
            final long from = ZonedDateTime.now().toEpochSecond() - lower;
            final long to = ZonedDateTime.now().toEpochSecond() + upper;

            if (value < from || value > to) {
                return ImmutableList.of(field + " clock-drift too big");
            }

            return ImmutableList.of();
        }

        public static Validator of(final String field, final long value, final long lower) {
            return of(field, value, lower, 0);
        }

        public static Validator of(final String field, final long value, final long lower, final long upper) {
            return new TimestampValidator(field, value, lower, upper);
        }
    }

    /**
     * Checks that {@link Object} is valid by calling {@link javax.validation.Validator}
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ValidValidator implements Validator {

        private final String field;
        private final Object object;
        private final javax.validation.Validator validator;

        @Override
        public List<String> valid() {
            return Optional.ofNullable(object).map(o -> validator.validate(o)).orElse(ImmutableSet.of()).stream()
                .map(cv -> field + "." + cv.getPropertyPath() + " " + cv.getMessage()).collect(Collectors.toList());
        }

        public static Validator of(final String field, final Object value) {
            return new ValidValidator(field, value, Validation.buildDefaultValidatorFactory().getValidator());
        }
    }
}
