package com.extole.consumer.rest.shareable.v4;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.model.RestError;

@Deprecated // TODO remove ENG-10127
public class CreateShareableV4Error extends RestError {
    public static final ErrorCode<CreateShareableV4Error> AUTHENTICATION_FAILED =
        new ErrorCode<>("AUTHENTICATION_FAILED", 403, "The user is not authorized to create shareables.");

    @JsonCreator
    protected CreateShareableV4Error(@JsonProperty("unique_id") String uniqueId,
        @JsonProperty("http_status_code") int httpStatusCode,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("parameters") Map<String, ? extends Object> parameters) {
        super(uniqueId, httpStatusCode, code, message, parameters);
    }

    public CreateShareableV4Error(RestError restError) {
        super(restError);
    }
}
