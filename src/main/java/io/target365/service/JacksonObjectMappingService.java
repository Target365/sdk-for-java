package io.target365.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.target365.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonObjectMappingService implements ObjectMappingService {

    private final ObjectMapper objectMapper;

    @Override
    public String toString(final Object object) {
        return Util.wrap(() -> objectMapper.writeValueAsString(object));
    }

    @Override
    public <T> T toObject(final String string, final Class<T> clazz) {
        return string == null ? null : Util.wrap(() -> objectMapper.readValue(string, clazz));
    }

    @Override
    public <T> T toObject(final String string, final TypeReference<T> typeReference) {
        return string == null ? null : Util.wrap(() -> objectMapper.readValue(string, typeReference));
    }

    public static JacksonObjectMappingService getInstance() {
        return new JacksonObjectMappingService(new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL));
    }
}
