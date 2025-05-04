package com.extole.common.rest.timezone;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ParamException;

public class ZonedDateTimeParamException extends ParamException {

    public ZonedDateTimeParamException(Throwable cause, String name, String defaultStringValue) {
        super(cause, Response.Status.BAD_REQUEST, QueryParam.class, name, defaultStringValue);
    }
}
