package com.extole.client.rest.label;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class LabelInjectorRestException extends ExtoleRestException {
    public static final ErrorCode<LabelInjectorRestException> LABEL_INJECTOR_NOT_FOUND =
        new ErrorCode<>("label_injector_not_found", 403, "Invalid label injector id", "label_injector_id");

    public LabelInjectorRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
