package com.extole.reporting.rest.posthandler.condition.exception;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.posthandler.ReportPostHandlerConditionValidationRestException;

public class MatchReportTagsPostHandlerConditionValidationRestException
    extends ReportPostHandlerConditionValidationRestException {

    public static final ErrorCode<MatchReportTagsPostHandlerConditionValidationRestException> MISSING_TAGS =
        new ErrorCode<>("report_post_handler_missing_tags", 400, "Tags cannot be empty.");

    public static final ErrorCode<MatchReportTagsPostHandlerConditionValidationRestException> INVALID_TAG =
        new ErrorCode<>("report_post_handler_invalid_tag", 400, "Tag cannot be empty or longer than 255 characters.",
            "tag");

    public MatchReportTagsPostHandlerConditionValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
