package com.extole.consumer.rest.impl.barcode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;

import com.extole.consumer.rest.barcode.BarcodeType;

@Provider
public final class BarcodeTypeConverterProvider implements ParamConverterProvider {

    private static final Map<Class<?>, ParamConverter<?>> CLASS_TO_CONVERTER =
        ImmutableMap.of(BarcodeType.class, new BarcodeTypeConverter());

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        return (ParamConverter<T>) CLASS_TO_CONVERTER.get(rawType);
    }
}
