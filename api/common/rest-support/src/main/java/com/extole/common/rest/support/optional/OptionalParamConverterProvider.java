package com.extole.common.rest.support.optional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;

@Singleton
public class OptionalParamConverterProvider implements ParamConverterProvider {

    private final InjectionManager injectionManager;

    @Inject
    public OptionalParamConverterProvider(InjectionManager injectionManager) {
        this.injectionManager = injectionManager;
    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (!Optional.class.equals(rawType)) {
            return null;
        }
        final ClassTypePair classTypePair = ReflectionHelper.getTypeArgumentAndClass(genericType)
            .stream()
            .findFirst()
            .orElse(null);

        if (classTypePair == null || classTypePair.rawClass() == String.class) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(final String value) {
                    return rawType.cast(Optional.ofNullable(value).filter(StringUtils::isNotEmpty));
                }

                @Override
                public String toString(final T value) {
                    return String.valueOf(value);
                }
            };
        }
        return Providers.getProviders(injectionManager, ParamConverterProvider.class)
            .stream()
            .map(provider -> provider.getConverter(classTypePair.rawClass(), classTypePair.type(), annotations))
            .filter(Objects::nonNull)
            .findFirst()
            .map(paramConverter -> new ParamConverter<T>() {
                @Override
                public T fromString(final String value) {
                    return rawType.cast(Optional.ofNullable(value)
                        .filter(StringUtils::isNotEmpty)
                        .map(paramConverter::fromString));
                }

                @Override
                public String toString(final T value) {
                    return String.valueOf(value);
                }
            }).orElse(null);
    }
}
