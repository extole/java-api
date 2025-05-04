package com.extole.consumer.rest.shareable.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove ENG-10127
public class GetShareableV4RestException extends ExtoleRestException {
    public static final ErrorCode<GetShareableV4RestException> NOT_FOUND =
        new ErrorCode<>("NOT_FOUND", 403, "Shareable Not Found.", "shareable_id");

    public GetShareableV4RestException(String uniqueId, ErrorCode<GetShareableV4RestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
