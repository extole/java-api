package com.extole.common.rest.exception;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RestExceptionBuilder<T extends RestException> {
    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionBuilder.class);

    private static final long LOW_BITS = 0xffffffffL;
    private static final int HIGH_BITS = 32;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MILLIS_IN_SECOND = 1000;

    private Throwable cause;
    private ErrorCode<T> errorCode = null;
    private final Map<String, Object> parameters;
    private final Class<T> exceptionClass;

    private RestExceptionBuilder(Class<T> klass) {
        this.parameters = new HashMap<>();
        this.exceptionClass = klass;
    }

    public static <S extends RestException> RestExceptionBuilder<S> newBuilder(Class<S> klass) {
        return new RestExceptionBuilder<>(klass);
    }

    public RestExceptionBuilder<T> withCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public RestExceptionBuilder<T> withErrorCode(ErrorCode<T> errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public RestExceptionBuilder<T> addParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public RestExceptionBuilder<T> addParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    public T build() {
        if (errorCode == null) {
            throw new UnbuildableRestRuntimeException("No ErrorCode specified");
        }

        T exception;
        try {
            exception = exceptionClass.getConstructor(String.class, ErrorCode.class, Map.class,
                Throwable.class).newInstance(generateUniqueId(), errorCode, parameters, cause);
        } catch (Throwable e) {
            // Catching all exceptions here as we don't want to propagate failures in our exception handling.
            LOG.error("Error building rest exception  with exceptionClass: {} errorCode: {} parameters: {} cause: {}",
                exceptionClass, errorCode, parameters, cause, e);
            LOG.error("Original cause of this error is: ", cause);
            throw new UnbuildableRestRuntimeException("Unable to find a appropriate constructor for " + exceptionClass,
                e);
        }

        List<String> errors = buildErrors();
        if (!errors.isEmpty()) {
            String joinedError = errors.stream().map(Object::toString).collect(Collectors.joining(","));
            LOG.error("BuiltException doesn't match ErrorCode(" + errorCode.getName() + ") definition, specifically("
                + joinedError + ") still thrown", exception);
        }
        return exception;
    }

    public boolean isValid() {
        return buildErrors().isEmpty();
    }

    private List<String> buildErrors() {
        List<String> errors = new ArrayList<>();

        try {
            boolean found = false;
            for (Field field : exceptionClass.getFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && errorCode == field.get(null)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                errors.add("unexpected_code:" + errorCode.getName());
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Problem checking built ExtoleRestException for code", e);
        }

        for (String name : errorCode.getAttributes()) {
            if (!parameters.containsKey(name)) {
                errors.add("missing_attribute:" + name);
            }
        }

        for (String parameter : parameters.keySet()) {
            if (!errorCode.getAttributes().contains(parameter)) {
                errors.add("extra_attribute:" + parameter);
            }
        }

        return errors;
    }

    private static String generateUniqueId() {
        return Long.valueOf(((System.currentTimeMillis() / MILLIS_IN_SECOND) << HIGH_BITS) +
            (RANDOM.nextInt() & LOW_BITS)).toString();
    }

}
