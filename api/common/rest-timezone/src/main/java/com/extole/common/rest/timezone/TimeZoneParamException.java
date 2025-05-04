package com.extole.common.rest.timezone;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ParamException;

import com.extole.common.rest.time.TimeZoneParam;

public class TimeZoneParamException extends ParamException {

    public TimeZoneParamException(Throwable cause, String name, String defaultValue) {
        super(cause, Response.Status.BAD_REQUEST, TimeZoneParam.class, name, defaultValue);
    }
}
