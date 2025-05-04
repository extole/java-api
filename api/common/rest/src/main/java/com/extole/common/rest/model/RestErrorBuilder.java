package com.extole.common.rest.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.owasp.encoder.Encode;

public class RestErrorBuilder {
    private String uniqueId;
    private String code;
    private int httpStatusCode;
    private String message;
    private Map<String, ? extends Object> parameters;

    public RestErrorBuilder() {

    }

    public RestErrorBuilder withUniqueId(String uniqueId) {
        this.uniqueId = Encode.forHtml(uniqueId);
        return this;
    }

    public RestErrorBuilder withCode(String code) {
        this.code = Encode.forHtml(code);
        return this;
    }

    public RestErrorBuilder withHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
        return this;
    }

    public RestErrorBuilder withMessage(String message) {
        this.message = Encode.forHtml(message);
        return this;
    }

    public RestErrorBuilder withParameters(Map<String, ? extends Object> parameters) {
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

    public RestError build() {
        return new RestError(uniqueId, httpStatusCode, code, message, parameters);
    }
}
