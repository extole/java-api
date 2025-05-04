package com.extole.common.rest.client.exception.translation.extole;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ws.rs.ClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.client.exception.translation.RestExceptionTranslationStrategy;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;

public final class ExtoleRestExceptionTranslationStrategy implements RestExceptionTranslationStrategy {

    private static final ExtoleRestExceptionTranslationStrategy SINGLETON =
        new ExtoleRestExceptionTranslationStrategy();
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    public ExtoleRestExceptionTranslationStrategy() {
    }

    @Override
    public Exception translateException(List<Class<? extends Exception>> expectedExceptionClasses,
        ClientErrorException clientRestException) {
        expectedExceptionClasses = ImmutableList.<Class<? extends Exception>>builder()
            .addAll(expectedExceptionClasses)
            .add(FatalRestRuntimeException.class)
            .add(WebApplicationRestRuntimeException.class)
            .build();

        RestExceptionResponse exceptionResponse;
        try (InputStream inputStream = clientRestException.getResponse().readEntity(InputStream.class)) {
            String exceptionAsString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            try {
                exceptionResponse = OBJECT_MAPPER.readValue(exceptionAsString, RestExceptionResponse.class);
            } catch (IOException e) {
                throw new BusinessExceptionDeserializationRuntimeException(
                    "Could not deserialize the rest exception=" + exceptionAsString, e);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not read body of rest exception=" + clientRestException.getResponse(), e);
        }

        Class<? extends Exception> businessExceptionClass =
            findBusinessExceptionClassByErrorCode(expectedExceptionClasses, exceptionResponse.getCode());
        if (businessExceptionClass == null) {
            throw new UnknownBusinessRuntimeException(exceptionResponse);
        }

        return newBusinessException(businessExceptionClass, exceptionResponse);
    }

    @Nullable
    private Class<? extends Exception> findBusinessExceptionClassByErrorCode(
        List<Class<? extends Exception>> expectedExceptionClasses, String errorCode) {

        for (Class<? extends Exception> exceptionClass : expectedExceptionClasses) {
            if (hasErrorCode(exceptionClass, errorCode)) {
                return exceptionClass;
            }
        }
        return null;
    }

    private boolean hasErrorCode(Class<? extends Exception> exceptionClass, String errorCode) {
        for (Field field : exceptionClass.getFields()) {
            int modifiers = field.getModifiers();
            if (ErrorCode.class.equals(field.getType())
                && Modifier.isPublic(modifiers)
                && Modifier.isStatic(modifiers)
                && Modifier.isFinal(modifiers)) {
                try {
                    ErrorCode<?> errorCodeObject = (ErrorCode<?>) field.get(null);
                    if (errorCode.equals(errorCodeObject.getName())) {
                        return true;
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // should not happen as we checked the type and modifiers before
                    throw new RuntimeException(
                        "Unable to read value of field " + exceptionClass.getName() + "." + field.getName(), e);
                }
            }
        }
        return false;
    }

    private Exception newBusinessException(Class<? extends Exception> businessExceptionClass,
        RestExceptionResponse exceptionResponse) {
        ErrorCode<?> errorCode = new ErrorCode<>(exceptionResponse.getCode(), exceptionResponse.getHttpStatusCode(),
            exceptionResponse.getMessage(), new String[0]);
        try {
            return businessExceptionClass.getConstructor(String.class, ErrorCode.class, Map.class, Throwable.class)
                .newInstance(exceptionResponse.getUniqueId(), errorCode, exceptionResponse.getParameters(), null);
        } catch (Exception e) {
            throw new InvalidBusinessRuntimeException("Unable to recreate the original business exception", e,
                exceptionResponse);
        }
    }

    public static ExtoleRestExceptionTranslationStrategy getSingleton() {
        return SINGLETON;
    }

}
