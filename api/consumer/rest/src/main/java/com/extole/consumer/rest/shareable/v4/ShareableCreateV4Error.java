package com.extole.consumer.rest.shareable.v4;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.model.RestError;

@Deprecated // TODO remove ENG-10127
public class ShareableCreateV4Error extends RestError {
    public static final ErrorCode<ShareableCreateV4Error> CREATION_FAILED =
        new ErrorCode<>("CREATION_FAILED", 403, "Shareable Creation Failed", "polling_id");

    @JsonCreator
    protected ShareableCreateV4Error(@JsonProperty("unique_id") String uniqueId,
        @JsonProperty("http_status_code") int httpStatusCode,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("parameters") Map<String, ? extends Object> parameters) {
        super(uniqueId, httpStatusCode, code, message, parameters);
    }

    public ShareableCreateV4Error(RestError restError) {
        super(restError);
    }
}
