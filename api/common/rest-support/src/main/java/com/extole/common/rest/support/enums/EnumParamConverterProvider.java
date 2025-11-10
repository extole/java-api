package com.extole.common.rest.support.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.apache.commons.lang3.EnumUtils;

@Singleton
@Priority(1)
public class EnumParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (Enum.class.isAssignableFrom(rawType)) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(final String value) {
                    if (value == null) {
                        return null;
                    }
                    String upperCaseValue = value.toUpperCase();
                    try {
                        Class<? extends Enum> enumClass = (Class<Enum>) Class.forName(genericType.getTypeName());
                        if (EnumUtils.isValidEnum(enumClass, upperCaseValue)) {
                            return (T) Enum.valueOf(enumClass, upperCaseValue);
                        }
                        throw new EnumParamException(genericType.getTypeName(), upperCaseValue,
                            Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList()));
                    } catch (ClassNotFoundException e) {
                        throw new EnumParamException(e, genericType.getTypeName());
                    }
                }

                @Override
                public String toString(final T value) {
                    return String.valueOf(value);
                }
            };
        }
        return null;
    }
}
