package com.extole.consumer.rest.impl.report.restclient;

import javax.ws.rs.WebApplicationException;

import com.extole.common.rest.model.RestExceptionResponse;

public class ReportRestRuntimeException extends WebApplicationException {

    public ReportRestRuntimeException(RestExceptionResponse exceptionResponse) {
        super(exceptionResponse.getCode() + " - " + exceptionResponse.getMessage()
            + (exceptionResponse.getParameters() != null ? " (" + exceptionResponse.getParameters() + ")" : ""),
            exceptionResponse.getHttpStatusCode());
    }
}
