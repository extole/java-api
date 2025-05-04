package com.extole.client.rest.campaign.incentive;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class IncentiveValidationRestException extends ExtoleRestException {

    public static final ErrorCode<IncentiveValidationRestException> NAME_INVALID_LENGTH =
        new ErrorCode<>("name_invalid_length", 403, "Name length length must be between 2 and 255 characters", "name");

    public static final ErrorCode<IncentiveValidationRestException> NAME_INVALID_CHARACTER =
        new ErrorCode<>("name_invalid_character", 403,
            "Incentive name can only contain alphanumeric, space, dash, colon and underscore", "name");

    public static final ErrorCode<IncentiveValidationRestException> DESCRIPTION_INVALID_LENGTH =
        new ErrorCode<>("description_invalid_length", 403,
            "Description length length must be between 2 and 255 characters", "description");

    public IncentiveValidationRestException(String uniqueId, ErrorCode<IncentiveValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
