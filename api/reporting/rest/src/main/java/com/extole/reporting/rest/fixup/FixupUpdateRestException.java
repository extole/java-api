package com.extole.reporting.rest.fixup;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupUpdateRestException extends ExtoleRestException {

    public static final ErrorCode<FixupUpdateRestException> NOT_EDITABLE =
        new ErrorCode<>("fixup_not_editable", 400, "Fixup is not editable");

    public FixupUpdateRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
