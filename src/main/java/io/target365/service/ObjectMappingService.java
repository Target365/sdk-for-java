package io.target365.service;

import com.fasterxml.jackson.core.type.TypeReference;

public interface ObjectMappingService {

    /**
     * Converts any object to a JSON string representation
     *
     * @param object Object to convert
     * @return JSON string representation of the object
     */
    String toString(final Object object);

    /**
     * Converts any JSON string representation to the object
     *
     * @param string JSON string representation
     * @param clazz  Type of the resulting object
     * @param <T>    Type of the resulting object
     * @return Object converted from the JSON string
     */
    <T> T toObject(final String string, final Class<T> clazz);

    /**
     * Converts any JSON string representation to the object
     *
     * @param string        JSON string representation
     * @param typeReference Type reference of the resulting object
     * @param <T>           Type of the resulting object
     * @return Object converted from the JSON string
     */
    <T> T toObject(final String string, final TypeReference<T> typeReference);

}
