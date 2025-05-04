package com.extole.common.rest.support.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.message.internal.FormProvider;
import org.json.JSONObject;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.support.filter.UrlEncodedFormHeaderInjectorFilter;

@Provider
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_FORM_URLENCODED)
public class UrlEncodedFormJacksonReader implements MessageBodyReader<Object> {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    private final FormProvider formProvider = new FormProvider();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type.isAssignableFrom(Optional.class) && genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                if (isCreatorAnnotationPresent(((Class<?>) actualTypeArguments[0]).getConstructors())) {
                    return true;
                }
            }
        }
        return isCreatorAnnotationPresent(type.getConstructors());
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        Form form = formProvider.readFrom(null, genericType, annotations, mediaType, httpHeaders, entityStream);
        MultivaluedMap<String, String> formParams = form.asMap();
        BeanMap rootMap = new BeanMap();

        for (String eachParameter : formParams.keySet()) {
            if (eachParameter.startsWith(UrlEncodedFormHeaderInjectorFilter.HEADERS_PREFIX + ".")) {
                continue;
            }
            String value = formParams.getFirst(eachParameter);
            if (value != null) {
                rootMap.setProperty(eachParameter, value);
            }
        }

        JSONObject jsonObject = new JSONObject(rootMap.asMap());
        Class<Object> valueType = type;
        if (type.isAssignableFrom(Optional.class) && genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                valueType = (Class<Object>) actualTypeArguments[0];
            }
        }

        return OBJECT_MAPPER.readValue(jsonObject.toString(), valueType);
    }

    private boolean isCreatorAnnotationPresent(Constructor<?>[] constructors) {
        return Arrays.stream(constructors)
            .anyMatch(constructor -> constructor.getAnnotation(JsonCreator.class) != null);
    }

}
