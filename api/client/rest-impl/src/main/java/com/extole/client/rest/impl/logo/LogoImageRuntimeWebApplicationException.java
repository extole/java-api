package com.extole.client.rest.impl.logo;

import javax.ws.rs.WebApplicationException;

public class LogoImageRuntimeWebApplicationException extends WebApplicationException {

    private final int statusCode;

    public LogoImageRuntimeWebApplicationException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
