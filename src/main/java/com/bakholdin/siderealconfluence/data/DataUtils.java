package com.bakholdin.siderealconfluence.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataUtils {

    public static <T> List<T> loadListFromResource(Resource resource, TypeReference<List<T>> typeReference) {
        ObjectMapper objectMapper = configureObjectMapper();
        try {
            return objectMapper.readValue(resource.getFile(), typeReference);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T obj) {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream);
            outputStream.writeObject(obj);
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
            ObjectInputStream inputStream = new ObjectInputStream(byteInputStream);
            return (T) inputStream.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @NonNull
    public static Map<String, Object> getSessionHeaders(SimpMessageHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() == null) {
            return new HashMap<>();
        }
        return accessor.getSessionAttributes();
    }

    public static String getSessionHeader(SimpMessageHeaderAccessor accessor, String key) {
        Map<String, Object> headers = getSessionHeaders(accessor);
        Object value = headers.get(key);
        return value == null ? null : value.toString();
    }

    @NonNull
    public static String getNonNullSessionHeader(SimpMessageHeaderAccessor accessor, String key) {
        Map<String, Object> headers = getSessionHeaders(accessor);
        Object value = headers.get(key);
        return value == null ? "" : value.toString();
    }
}
