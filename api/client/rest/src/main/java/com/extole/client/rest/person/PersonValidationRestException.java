package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonValidationRestException extends ExtoleRestException {

    public static final ErrorCode<PersonValidationRestException> IDENTITY_KEY_VALUE_INVALID =
        new ErrorCode<>("invalid_key_value", 400, "Invalid key value", "identity_key_value");

    public static final ErrorCode<PersonValidationRestException> IDENTITY_KEY_VALUE_ALREADY_TAKEN =
        new ErrorCode<>("identity_key_value_already_taken", 400, "Identity key value already taken",
            "identity_key_value");

    public static final ErrorCode<PersonValidationRestException> INVALID_BLOCK_REASON =
        new ErrorCode<>("invalid_block_reason", 400, "Block reason can't be empty");

    public PersonValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
