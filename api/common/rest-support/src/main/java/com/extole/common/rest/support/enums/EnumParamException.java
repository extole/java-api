package com.extole.common.rest.support.enums;

import java.util.List;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ParamException;

public class EnumParamException extends ParamException {

    private final String providedValue;
    private final List<String> allowedValues;

    public EnumParamException(String name, String providedValue, List<String> allowedValues) {
        super(null, Response.Status.BAD_REQUEST, QueryParam.class, name, null);
        this.allowedValues = allowedValues;
        this.providedValue = providedValue;
    }

    public EnumParamException(Throwable cause, String name) {
        super(cause, Response.Status.BAD_REQUEST, QueryParam.class, name, null);
        this.allowedValues = List.of();
        this.providedValue = null;
    }

    public String getProvidedValue() {
        return providedValue;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }
}
