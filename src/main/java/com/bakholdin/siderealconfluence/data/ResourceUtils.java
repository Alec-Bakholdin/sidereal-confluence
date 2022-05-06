package com.bakholdin.siderealconfluence.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class ResourceUtils {

    public static <T> List<T> loadListFromResource(Resource resource, TypeReference<List<T>> typeReference) {
        ObjectMapper objectMapper = configureObjectMapper();
        try {
            return objectMapper.readValue(resource.getFile(), typeReference);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
