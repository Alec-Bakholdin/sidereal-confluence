package com.bakholdin.siderealconfluence.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class InitUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> List<T> readListFromResource(Resource resource, TypeReference<List<T>> typeReference) {
        try {
            return MAPPER.readValue(resource.getFile(), typeReference);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
