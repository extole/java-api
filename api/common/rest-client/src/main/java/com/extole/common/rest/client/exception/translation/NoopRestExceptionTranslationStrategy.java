package com.extole.common.rest.client.exception.translation;

import java.util.List;

import javax.ws.rs.ClientErrorException;

public final class NoopRestExceptionTranslationStrategy implements RestExceptionTranslationStrategy {

    private static final NoopRestExceptionTranslationStrategy SINGLETON = new NoopRestExceptionTranslationStrategy();

    private NoopRestExceptionTranslationStrategy() {
    }

    @Override
    public Exception translateException(List<Class<? extends Exception>> expectedExceptionClasses,
        ClientErrorException clientRestException) {
        return clientRestException;
    }

    public static NoopRestExceptionTranslationStrategy getSingleton() {
        return SINGLETON;
    }

}
