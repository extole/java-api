package com.extole.common.rest;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;

public final class JacksonExceptionRestTranslator {

    private static final Map<Class<? extends JacksonException>,
        BiConsumer<? super JacksonException, RestExceptionBuilder<WebApplicationRestRuntimeException>>> TRANSLATORS =
            ImmutableMap
                .<Class<? extends JacksonException>,
                    BiConsumer<? super JacksonException,
                        RestExceptionBuilder<WebApplicationRestRuntimeException>>>builder()
                .put(JsonParseException.class, JacksonExceptionRestTranslator::toJsonParseRestException)
                .put(InvalidFormatException.class, JacksonExceptionRestTranslator::toJsonMalformedRestException)
                .put(InvalidTypeIdException.class, JacksonExceptionRestTranslator::toJsonInvalidTypeIdRestException)
                .put(UnrecognizedPropertyException.class,
                    JacksonExceptionRestTranslator::toJsonUnrecognizedPropertyRestException)
                .build();

    public <T extends JacksonException> RestException translate(T exception) {
        BiConsumer<? super JacksonException, RestExceptionBuilder<WebApplicationRestRuntimeException>> translator =
            TRANSLATORS
                .getOrDefault(exception.getClass(), JacksonExceptionRestTranslator::toInvalidJsonDefaultRestException);

        RestExceptionBuilder<WebApplicationRestRuntimeException> restExceptionBuilder = RestExceptionBuilder
            .newBuilder(WebApplicationRestRuntimeException.class)
            .withCause(exception);

        translator.accept(exception, restExceptionBuilder);

        return restExceptionBuilder.build();
    }

    private static <T extends JacksonException> void toInvalidJsonDefaultRestException(T exception,
        RestExceptionBuilder<WebApplicationRestRuntimeException> exceptionBuilder) {

        exceptionBuilder
            .addParameter("location", errorLocation(exception).orElse(StringUtils.EMPTY))
            .withErrorCode(WebApplicationRestRuntimeException.INVALID_JSON);
    }

    private static <T extends JacksonException> void toJsonParseRestException(T exception,
        RestExceptionBuilder<WebApplicationRestRuntimeException> exceptionBuilder) {

        JsonParseException currentException = (JsonParseException) exception;
        String detailedMessage = currentException.getOriginalMessage();

        exceptionBuilder
            .addParameter("detailed_message", detailedMessage)
            .addParameter("location", errorLocation(exception).orElse(StringUtils.EMPTY))
            .withErrorCode(WebApplicationRestRuntimeException.INVALID_JSON_NON_PARSEABLE);
    }

    private static <T extends JacksonException> void toJsonMalformedRestException(T exception,
        RestExceptionBuilder<WebApplicationRestRuntimeException> exceptionBuilder) {
        InvalidFormatException currentException = (InvalidFormatException) exception;

        String invalidPropertyValue = currentException.getValue().toString();
        Optional<JsonMappingException.Reference> reference = currentException.getPath().stream().findFirst();
        String invalidProperty = reference.isPresent() ? reference.get().getFieldName() : "";

        Map<String, Object> parameters = ImmutableMap.of(
            "invalid_property", invalidProperty,
            "invalid_value", invalidPropertyValue,
            "location", errorLocation(exception).orElse(StringUtils.EMPTY));

        exceptionBuilder
            .addParameters(parameters)
            .withErrorCode(WebApplicationRestRuntimeException.INVALID_JSON_MALFORMED);
    }

    private static <T extends JacksonException> void toJsonInvalidTypeIdRestException(T exception,
        RestExceptionBuilder<WebApplicationRestRuntimeException> exceptionBuilder) {
        InvalidTypeIdException currentException = (InvalidTypeIdException) exception;

        Optional<JsonMappingException.Reference> reference = currentException.getPath().stream().findFirst();
        String invalidProperty = reference.isPresent() ? reference.get().getFieldName() : "";

        Map<String, Object> parameters = ImmutableMap.of(
            "invalid_property", invalidProperty,
            "location", errorLocation(exception).orElse(StringUtils.EMPTY));

        exceptionBuilder
            .addParameters(parameters)
            .withErrorCode(WebApplicationRestRuntimeException.INVALID_JSON_UNKNOWN_TYPE_ID);
    }

    private static <T extends JacksonException> void
        toJsonUnrecognizedPropertyRestException(T exception,
            RestExceptionBuilder<WebApplicationRestRuntimeException> exceptionBuilder) {
        UnrecognizedPropertyException currentException = (UnrecognizedPropertyException) exception;

        StringJoiner getKnownPropertiesJoiner = new StringJoiner(",", "[", "]");
        currentException.getKnownPropertyIds().forEach(p -> getKnownPropertiesJoiner.add(p.toString()));

        Map<String, Object> parameters = ImmutableMap.of(
            "unrecognized_property", currentException.getPropertyName(),
            "known_properties", getKnownPropertiesJoiner.toString(),
            "location", errorLocation(exception).orElse(StringUtils.EMPTY));

        exceptionBuilder
            .addParameters(parameters)
            .withErrorCode(WebApplicationRestRuntimeException.INVALID_JSON_UNRECOGNIZED_PROPERTY);
    }

    private static Optional<String> errorLocation(JacksonException e) {
        JsonLocation location = e.getLocation();

        if (location == null) {
            return Optional.empty();
        }

        return Optional.of(String
            .format("{line: %d, column: %d}",
                Integer.valueOf(location.getLineNr()),
                Integer.valueOf(location.getColumnNr())));
    }
}
