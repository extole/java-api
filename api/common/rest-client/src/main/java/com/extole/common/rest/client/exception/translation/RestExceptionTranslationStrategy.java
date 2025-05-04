package com.extole.common.rest.client.exception.translation;

import java.util.List;

import javax.ws.rs.ClientErrorException;

public interface RestExceptionTranslationStrategy {

    Exception translateException(List<Class<? extends Exception>> expectedExceptionClasses,
        ClientErrorException clientRestException);

}
