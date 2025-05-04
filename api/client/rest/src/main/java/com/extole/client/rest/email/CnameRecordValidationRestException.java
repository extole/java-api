package com.extole.client.rest.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CnameRecordValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CnameRecordValidationRestException> INVALID_ALIAS =
        new ErrorCode<>("invalid_alias", 400, "Invalid alias", "value");

    public static final ErrorCode<CnameRecordValidationRestException> INVALID_CANONICAL_NAME =
        new ErrorCode<>("invalid_canonical_name", 400, "Invalid canonical name", "value");

    public static final ErrorCode<CnameRecordValidationRestException> MISSING_REQUIRED_FIELD =
        new ErrorCode<>("missing_required_field", 400, "Missing required field", "field_name");

    public static final ErrorCode<CnameRecordValidationRestException> TOO_LONG_FIELD =
        new ErrorCode<>("too_long_cname_record_field", 400, "CnameRecord to long field", "max_length", "field_name");

    public static final ErrorCode<CnameRecordValidationRestException> DUPLICATE_CNAME_ALIAS =
        new ErrorCode<>("duplicate_cname_alias", 400, "Duplicate cname alias", "alias");

    public CnameRecordValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
