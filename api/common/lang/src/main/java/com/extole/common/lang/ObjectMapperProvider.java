package com.extole.common.lang;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.extole.common.lang.date.ExtoleTimeModule;

public final class ObjectMapperProvider {

    private static final ObjectMapper BASIC_INSTANCE = new ObjectMapper();

    private static final ObjectMapper INSTANCE = constructConfiguredInstance();

    private static final ObjectMapper TIME_ZONE_INSTANCE = constructConfiguredInstance()
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static ObjectMapper getTimeZoneInstance() {
        return TIME_ZONE_INSTANCE;
    }

    public static ObjectMapper getConfiguredInstance() {
        return INSTANCE;
    }

    public static ObjectMapper getInstance() {
        return BASIC_INSTANCE;
    }

    @Deprecated // TODO replace with specifically named instances in pt 2 of ENG-16275
    public static ObjectMapper buildCustomMapper() {
        return new ObjectMapper();
    }

    private ObjectMapperProvider() {

    }

    private static ObjectMapper constructConfiguredInstance() {
        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new ExtoleTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new GuavaModule());
    }
}
