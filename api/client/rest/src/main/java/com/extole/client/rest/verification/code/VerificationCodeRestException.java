package com.extole.client.rest.verification.code;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class VerificationCodeRestException extends ExtoleRestException {

    public static final ErrorCode<VerificationCodeRestException> VERIFICATION_CODE_NOT_PROVIDED =
        new ErrorCode<>("missing_verification_code", 400, "Verification code not provided");

    public static final ErrorCode<VerificationCodeRestException> VERIFICATION_CODE_NOT_FOUND =
        new ErrorCode<>("verification_code_not_found", 400, "Verification code not found", "bad_code");

    public VerificationCodeRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
