package com.extole.client.rest.prehandler;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PrehandlerRestException extends ExtoleRestException {

    public static final ErrorCode<PrehandlerRestException> PREHANDLER_NOT_FOUND =
        new ErrorCode<>("prehandler_not_found", 400, "Prehandler not found", "prehandler_id");

    public PrehandlerRestException(String uniqueId, ErrorCode<PrehandlerRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
