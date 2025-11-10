package com.extole.common.rest.support.request.resolver;

public class MissingSubTypesAnnotationException extends RuntimeException {

    private static final String MESSAGE_PATTERN = "Class: %s is not annotated with: %s annotation.";

    public MissingSubTypesAnnotationException(Class<?> requestClass, Class<?> annotation) {
        super(String.format(MESSAGE_PATTERN, requestClass.getCanonicalName(), annotation.getCanonicalName()));
    }
}
