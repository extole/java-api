package com.extole.reporting.rest.fixup;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupValidationRestException extends ExtoleRestException {

    public static final ErrorCode<FixupValidationRestException> MISSING_NAME =
        new ErrorCode<>("missing_name", 400, "Fixup missing name");

    public static final ErrorCode<FixupValidationRestException> MISSING_DATA_SOURCE =
        new ErrorCode<>("missing_data_source", 400, "Fixup missing dataSource");

    public FixupValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
