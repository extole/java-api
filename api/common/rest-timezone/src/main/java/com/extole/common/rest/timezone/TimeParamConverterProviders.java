package com.extole.common.rest.timezone;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import com.google.common.collect.ImmutableMap;

import com.extole.common.rest.model.RequestContextAttributeName;

@Singleton
final class TimeParamConverterProviders implements ParamConverterProvider {

    private final Map<Class<?>, ParamConverter<?>> classToConverter;

    @Inject
    TimeParamConverterProviders(Provider<ContainerRequestContext> containerRequestProvider) {
        this.classToConverter =
            ImmutableMap.of(ZoneId.class, new ZoneIdParamConverter(),
                ZonedDateTime.class,
                new ZonedDateTimeParamConverter(() -> {
                    Object property = containerRequestProvider
                        .get()
                        .getProperty(RequestContextAttributeName.REQUEST_TIME_ZONE.getAttributeName());
                    if (property == null) {
                        return Optional.empty();
                    }
                    return Optional.of((ZoneId) property);
                }));
    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        return (ParamConverter<T>) classToConverter.get(rawType);
    }
}
