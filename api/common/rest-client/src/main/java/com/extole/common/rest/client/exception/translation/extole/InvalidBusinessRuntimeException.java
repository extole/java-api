package com.extole.common.rest.client.exception.translation.extole;

import com.extole.common.rest.model.RestExceptionResponse;

public class InvalidBusinessRuntimeException extends RuntimeException {

    private final RestExceptionResponse exceptionResponse;

    public InvalidBusinessRuntimeException(String message, Exception cause, RestExceptionResponse exceptionResponse) {
        super(message, cause);
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
