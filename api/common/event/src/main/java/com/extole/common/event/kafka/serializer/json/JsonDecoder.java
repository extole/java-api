package com.extole.common.event.kafka.serializer.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.extole.common.event.kafka.serializer.exception.DeserializerRuntimeException;

public class JsonDecoder<T> {
    private final Class<T> valueType;
    private final ObjectMapper mapper;

    public JsonDecoder(Class<T> valueType, ObjectMapper mapper) {
        this.valueType = valueType;
        this.mapper = mapper;
    }

    public T decode(byte[] event) {
        try {
            return mapper.readValue(event, valueType);
        } catch (IOException e) {
            throw new DeserializerRuntimeException("Failed to decode event: " + event + " to class type: " + valueType,
                e);
        }
    }
}
