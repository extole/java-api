package com.extole.common.rest.omissible;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OmissibleRestException extends ExtoleRestException {

    public static final ErrorCode<OmissibleRestException> INVALID_NULL =
        new ErrorCode<>("invalid_null", 400,
            "The attribute may be omitted but not nullified", "attribute_name");

    public OmissibleRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
