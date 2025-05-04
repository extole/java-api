package com.extole.consumer.rest.share;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ShareRestException extends ExtoleRestException {

    public static final ErrorCode<ShareRestException> SHARE_NOT_FOUND = new ErrorCode<>(
        "SHARE_NOT_FOUND", 400, "Share not found.", "share_id");

    public ShareRestException(String uniqueId, ErrorCode<ShareRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
