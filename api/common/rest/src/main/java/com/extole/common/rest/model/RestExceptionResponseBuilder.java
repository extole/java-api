package com.extole.common.rest.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.owasp.encoder.Encode;

import com.extole.common.rest.exception.RestException;

public class RestExceptionResponseBuilder {
    private String uniqueId;
    private String code;
    private int httpStatusCode;
    private String message;
    private Map<String, ? extends Object> parameters;

    public RestExceptionResponseBuilder(RestException restException) {
        withUniqueId(String.valueOf(restException.getUniqueId()));
        withHttpStatusCode(restException.getHttpStatusCode());
        withCode(restException.getErrorCode());
        withMessage(restException.getMessage());
        withParameters(restException.getParameters());
    }

    public RestExceptionResponseBuilder() {

    }

    public RestExceptionResponseBuilder withUniqueId(String uniqueId) {
        this.uniqueId = Encode.forHtml(uniqueId);
        return this;
    }

    public RestExceptionResponseBuilder withCode(String code) {
        this.code = Encode.forHtml(code);
        return this;
    }

    public RestExceptionResponseBuilder withHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
        return this;
    }

    public RestExceptionResponseBuilder withMessage(String message) {
        this.message = Encode.forHtml(message);
        return this;
    }

    public RestExceptionResponseBuilder withParameters(Map<String, ? extends Object> parameters) {
        if (parameters != null) {
            this.parameters = parameters.entrySet().stream().collect(HashMap::new,
                (map, entry) -> {
                    if (entry.getValue() != null) {
                        if (entry.getValue() instanceof CharSequence) {
                            map.put(entry.getKey(), Encode.forHtml(String.valueOf(entry.getValue())));
                        } else {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                },
                HashMap::putAll);
        } else {
            this.parameters = Collections.emptyMap();
        }
        return this;
    }

    public RestExceptionResponse build() {
        return new RestExceptionResponse(uniqueId, httpStatusCode, code, message, parameters);
    }
}
