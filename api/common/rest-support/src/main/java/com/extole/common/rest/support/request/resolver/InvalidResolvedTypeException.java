package com.extole.common.rest.support.request.resolver;

public class InvalidResolvedTypeException extends RuntimeException {

    private static final String MESSAGE_PATTERN =
        "Class: %s, does not contain resolved type: %s, in %s annotation definition.";

    public InvalidResolvedTypeException(Class<?> requestClass, String resolvedType, Class<?> annotation) {
        super(String.format(MESSAGE_PATTERN, requestClass.getCanonicalName(), resolvedType,
            annotation.getCanonicalName()));
    }
}
