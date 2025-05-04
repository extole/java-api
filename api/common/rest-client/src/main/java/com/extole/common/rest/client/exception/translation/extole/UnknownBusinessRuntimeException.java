package com.extole.common.rest.client.exception.translation.extole;

import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;

public class UnknownBusinessRuntimeException extends ExtoleRestRuntimeException {

    private final RestExceptionResponse exceptionResponse;

    public UnknownBusinessRuntimeException(RestExceptionResponse exceptionResponse) {
        super(exceptionResponse.getUniqueId(),
            new ErrorCode(exceptionResponse.getCode(), exceptionResponse.getHttpStatusCode(),
                exceptionResponse.getMessage()),
            exceptionResponse.getParameters() != null ? ImmutableMap.copyOf(exceptionResponse.getParameters())
                : Collections.emptyMap(),
            null);
        this.exceptionResponse = exceptionResponse;
    }

    public RestExceptionResponse getExceptionResponse() {
        return exceptionResponse;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + exceptionResponse.toString();
    }

}
