package io.target365.util;

import lombok.experimental.UtilityClass;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@UtilityClass
public final class Util {

    /**
     * Safely encode string without throwing checked exception
     *
     * @param string String to encode
     * @return Encoded string
     */
    public static String safeEncode(final String string) {
        return wrap(() -> URLEncoder.encode(string, StandardCharsets.UTF_8.toString()));
    }

    /**
     * Gets last element in the array
     *
     * @param t   Array
     * @param <T> Type of elements in the array
     * @return Last element in the array
     */
    public static <T> T getLast(final T[] t) {
        return t[t.length - 1];
    }

    /**
     * Wraps supplier which can throws {@link Exception} and converts it to {@link RuntimeException} if thrown
     *
     * @param supplier Supplier which potentially can throw an {@link Exception}
     * @param <T>      Type of the supplier result
     * @return Result provided by the supplier or throw {@link RuntimeException}
     */
    public static <T> T wrap(final Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Wraps supplier which can throws {@link Exception} and returns <code>null</code> if thrown
     *
     * @param supplier Supplier which potentially can throw an {@link Exception}
     * @param <T>      Type of the supplier result
     * @return Result provided by the supplier or null
     */
    public static <T> T suppress(final Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return null;
        }
    }

    @FunctionalInterface
    public interface Supplier<T> {
        T get() throws Throwable;
    }

}
