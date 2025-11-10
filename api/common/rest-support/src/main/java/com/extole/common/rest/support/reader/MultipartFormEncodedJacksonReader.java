package com.extole.common.rest.support.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.server.ContainerRequest;
import org.json.JSONObject;

import com.extole.common.lang.ObjectMapperProvider;

@Provider
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.MULTIPART_FORM_DATA)
public class MultipartFormEncodedJacksonReader implements MessageBodyReader<Object> {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    @Inject
    private javax.inject.Provider<ContainerRequest> requestProvider;

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
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
        throws IOException, WebApplicationException {

        ContainerRequest request = requestProvider.get();

        FormDataMultiPart entity = request.readEntity(FormDataMultiPart.class);
        if (entity == null) {
            throw new BadRequestException(LocalizationMessages.ENTITY_IS_EMPTY());
        }

        BeanMap rootMap = new BeanMap();
        for (String parameter : entity.getFields().keySet()) {
            FormDataBodyPart currentField = entity.getField(parameter);

            if (MediaType.TEXT_PLAIN_TYPE.equals(currentField.getMediaType())) {
                String value = currentField.getValue();
                if (value != null) {
                    rootMap.setProperty(parameter, value);
                }
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
