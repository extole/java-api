package com.extole.common.rest.model;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.extole.common.lang.ToString;

@JsonPropertyOrder({"unique_id", "http_status_code", "code", "message", "parameters"})
public class RestError {
    private final String uniqueId;
    private final String code;
    private final int httpStatusCode;
    private final String message;
    private final Map<String, ? extends Object> parameters;

    protected RestError(
        @JsonProperty("unique_id") String uniqueId,
        @JsonProperty("http_status_code") int httpStatusCode,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("parameters") Map<String, ? extends Object> parameters) {
        this.uniqueId = uniqueId;
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    protected RestError(RestError restError) {
        this.uniqueId = restError.getUniqueId();
        this.httpStatusCode = restError.getHttpStatusCode();
        this.code = restError.getCode();
        this.message = restError.getMessage();
        this.parameters = Collections.unmodifiableMap(restError.getParameters());
    }

    @JsonProperty("unique_id")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty("http_status_code")
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("parameters")
    public Map<String, ? extends Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
