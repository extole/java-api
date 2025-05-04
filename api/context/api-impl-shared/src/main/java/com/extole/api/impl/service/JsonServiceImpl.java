package com.extole.api.impl.service;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.service.JsonService;
import com.extole.common.lang.date.ExtoleTimeModule;

public class JsonServiceImpl implements JsonService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new ExtoleTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

    private static final Logger LOG = LoggerFactory.getLogger(JsonServiceImpl.class);

    @Nullable
    @Override
    public Object readJsonPath(Object json, String jsonPath) {
        try {
            return JsonPath.read(json, jsonPath);
        } catch (PathNotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            LOG.error("Unable to evaluate JSON path: {} on the object of type: {} with this value: {}", jsonPath,
                json.getClass(), json, e);
            return null;
        }
    }

    @Override
    public String toJsonString(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to write object as JSON string: {}", object, e);
            return null;
        }
    }

    @Override
    public Object toJsonObject(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(jsonString, Object.class);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to deserialize JSON string to object: {}", jsonString, e);
            return null;
        }
    }

}
