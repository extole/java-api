package com.extole.client.rest.prehandler.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO to be removed in ENG-13399
public class PrehandlerV2RestException extends ExtoleRestException {

    public static final ErrorCode<PrehandlerV2RestException> INVALID_PREHANDLER_ID = new ErrorCode<>(
        "invalid_prehandler_id", 400, "Invalid Prehandler Id", "prehandler_id", "client_id");

    public PrehandlerV2RestException(String uniqueId, ErrorCode<PrehandlerV2RestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
