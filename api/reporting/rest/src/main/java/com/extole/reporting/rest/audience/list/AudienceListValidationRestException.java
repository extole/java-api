package com.extole.reporting.rest.audience.list;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceListValidationRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceListValidationRestException> EMPTY_NAME =
        new ErrorCode<>("audience_list_name_empty", 400, "Name can't be empty");

    public static final ErrorCode<AudienceListValidationRestException> NAME_TOO_LONG =
        new ErrorCode<>("audience_list_name_too_long", 400, "Name max length is 255");

    public static final ErrorCode<AudienceListValidationRestException> DESCRIPTION_TOO_LONG =
        new ErrorCode<>("audience_list_description_too_long", 400, "Description max length is 1024");

    public AudienceListValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
