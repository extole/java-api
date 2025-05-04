package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupFilterUpdateRestException extends ExtoleRestException {

    public static final ErrorCode<FixupFilterUpdateRestException> NOT_EDITABLE =
        new ErrorCode<>("fixup_filter_not_editable", 400, "Fixup filter is not editable");

    public FixupFilterUpdateRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
