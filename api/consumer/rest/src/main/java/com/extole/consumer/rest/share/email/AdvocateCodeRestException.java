package com.extole.consumer.rest.share.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AdvocateCodeRestException extends ExtoleRestException {

    public static final ErrorCode<AdvocateCodeRestException> ADVOCATE_CODE_MISSING = new ErrorCode<>(
        "ADVOCATE_CODE_MISSING", 400, "advocate_code is required in the share request.");

    public static final ErrorCode<AdvocateCodeRestException> ADVOCATE_CODE_NOT_FOUND = new ErrorCode<>(
        "ADVOCATE_CODE_NOT_FOUND", 400, "Advocate code not found", "advocate_code");

    public AdvocateCodeRestException(String uniqueId, ErrorCode<AdvocateCodeRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
