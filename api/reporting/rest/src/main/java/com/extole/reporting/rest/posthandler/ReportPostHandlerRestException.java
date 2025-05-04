package com.extole.reporting.rest.posthandler;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportPostHandlerRestException extends ExtoleRestException {

    public static final ErrorCode<ReportPostHandlerRestException> NOT_FOUND =
        new ErrorCode<>("report_post_handler_not_found", 400, "Report post handler not found", "id");

    public ReportPostHandlerRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
