package com.extole.api.step.action.display;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ApiResponseImpl implements ApiResponse {

    private static final String JSON_BODY = "body";
    private static final String JSON_HEADERS = "headers";
    private static final String JSON_STATUS_CODE = "status_code";

    private final String body;
    private final Map<String, String> headers;
    private final int statusCode;

    public ApiResponseImpl(@JsonProperty(JSON_BODY) String body,
        @JsonProperty(JSON_HEADERS) Map<String, String> headers,
        @JsonProperty(JSON_STATUS_CODE) int statusCode) {
        this.body = body;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    @Override
    @JsonProperty(JSON_BODY)
    public String getBody() {
        return body;
    }

    @Override
    @JsonProperty(JSON_HEADERS)
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    @JsonProperty(JSON_STATUS_CODE)
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
