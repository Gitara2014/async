package com.bane.asynch.util;

import com.bane.asynch.domain.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T getResponseInObject(final String response, final Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(response, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json read value error");
        }
    }

    public static String getRequestBodyAsString(final String url) throws JsonProcessingException {
        Request request = Request.builder()
                .url(url)
                .origin("112.196.145.87")
                .build();
        return objectMapper.writeValueAsString(request);
    }


}
