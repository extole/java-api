package com.extole.consumer.rest.me.shareable;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ShareableRestException extends ExtoleRestException {
    public static final ErrorCode<ShareableRestException> SHAREABLE_NOT_FOUND =
        new ErrorCode<>("shareable_not_found", 403, "Shareable not found.", "code");

    public ShareableRestException(String uniqueId, ErrorCode<ShareableRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
