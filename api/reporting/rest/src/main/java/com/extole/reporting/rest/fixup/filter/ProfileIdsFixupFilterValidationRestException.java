package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProfileIdsFixupFilterValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ProfileIdsFixupFilterValidationRestException> FILTER_PROFILE_IDS_INVALID =
        new ErrorCode<>("filter_profile_ids_invalid", 400, "Filter ProfileIds is invalid");

    public ProfileIdsFixupFilterValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
