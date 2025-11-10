package com.extole.common.rest.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

@Schema(description = "Represents the API error response")
@JsonPropertyOrder({RestExceptionResponse.JSON_UNIQUE_ID, RestExceptionResponse.JSON_HTTP_STATUS_CODE,
    RestExceptionResponse.JSON_CODE, RestExceptionResponse.JSON_MESSAGE, RestExceptionResponse.JSON_PARAMETERS})
public class RestExceptionResponse {
    public static final String JSON_UNIQUE_ID = "unique_id";
    public static final String JSON_HTTP_STATUS_CODE = "http_status_code";
    public static final String JSON_CODE = "code";
    public static final String JSON_MESSAGE = "message";
    public static final String JSON_PARAMETERS = "parameters";

    private final String uniqueId;
    private final String code;
    private final int httpStatusCode;
    private final String message;
    private final Map<String, ? extends Object> parameters;

    protected RestExceptionResponse(
        @JsonProperty(JSON_UNIQUE_ID) String uniqueId,
        @JsonProperty(JSON_HTTP_STATUS_CODE) int httpStatusCode,
        @JsonProperty(JSON_CODE) String code,
        @JsonProperty(JSON_MESSAGE) String message,
        @JsonProperty(JSON_PARAMETERS) Map<String, ? extends Object> parameters) {

        this.uniqueId = uniqueId;
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_UNIQUE_ID)
    @Schema(description = "Unique id associated with this error, useful for discussions with Extole")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty(JSON_HTTP_STATUS_CODE)
    @Schema(description = "HTTP status code that was returned with this error, useful if client get response code")
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @JsonProperty(JSON_CODE)
    @Schema(description = "Specific error code for this error type, documented per endpoint")
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_MESSAGE)
    @Schema(description = "User readable English description of the error")
    public String getMessage() {
        return message;
    }

    @JsonProperty(JSON_PARAMETERS)
    @Schema(description = "Attributes related to the error, varies be error code, documented per endpoint")
    public Map<String, ? extends Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
