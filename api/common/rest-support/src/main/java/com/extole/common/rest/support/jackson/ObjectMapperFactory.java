package com.extole.common.rest.support.jackson;

import java.time.ZoneId;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.glassfish.hk2.api.Factory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.evaluateable.ValidEvaluatableModule;

@Provider
public class ObjectMapperFactory implements Factory<ObjectMapper> {

    private final ObjectMapper objectMapper;

    @Inject
    public ObjectMapperFactory(javax.inject.Provider<ContainerRequestContext> containerRequestProvider) {
        this.objectMapper = Jackson2ObjectMapperBuilder
            .json()
            .failOnUnknownProperties(true)
            .modules(
                new Jdk8Module(),
                new ExtoleTimeModule(() -> {
                    Object property = containerRequestProvider
                        .get()
                        .getProperty(RequestContextAttributeName.REQUEST_TIME_ZONE.getAttributeName());
                    if (property == null) {
                        return Optional.empty();
                    }
                    return Optional.of((ZoneId) property);
                }),
                new ValidEvaluatableModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();
    }

    @Override
    public ObjectMapper provide() {
        return objectMapper;
    }

    @Override
    public void dispose(ObjectMapper instance) {
    }
}
