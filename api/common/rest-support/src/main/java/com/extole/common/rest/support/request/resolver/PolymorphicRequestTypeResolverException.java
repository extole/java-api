package com.extole.common.rest.support.request.resolver;

public class PolymorphicRequestTypeResolverException extends Exception {

    public PolymorphicRequestTypeResolverException(String message) {
        super(message);
    }

    public PolymorphicRequestTypeResolverException(String message, Throwable cause) {
        super(message, cause);
    }
}
