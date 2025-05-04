package com.extole.reporting.rest.audience.list;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceListRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceListRestException> NOT_FOUND =
        new ErrorCode<>("audience_list_not_found", 404, "Audience list not found", "audience_list_id");

    public static final ErrorCode<AudienceListRestException> SOURCE_NOT_FOUND =
        new ErrorCode<>("audience_list_source_not_found", 400, "Audience List source not found, check " +
            "that configured report or ReportRunner is present", "audience_list_id");

    public static final ErrorCode<AudienceListRestException> UPLOADED_REFRESH_ERROR =
        new ErrorCode<>("audience_list_refresh_error", 400, "Unable to refresh audience list it is not refreshable",
            "audience_list_id");

    public static final ErrorCode<AudienceListRestException> REFRESH_ERROR =
        new ErrorCode<>("audience_list_refresh_error", 400, "Unable to refresh audience list", "audience_list_id");

    public static final ErrorCode<AudienceListRestException> CONTENT_NOT_AVAILABLE =
        new ErrorCode<>("audience_list_content_not_available", 400, "Content not available", "audience_list_id",
            "format");

    public static final ErrorCode<AudienceListRestException> FORMAT_NOT_AVAILABLE =
        new ErrorCode<>("audience_list_format_not_available", 400, "Content not available in requested format",
            "audience_list_id", "format");

    public static final ErrorCode<AudienceListRestException> FORMAT_NOT_SUPPORTED =
        new ErrorCode<>("audience_list_format_not_supported", 400, "Format not supported",
            "audience_list_id", "format");

    public static final ErrorCode<AudienceListRestException> PREVIEW_NOT_AVAILABLE =
        new ErrorCode<>("audience_list_preview_not_available", 400, "Preview not available",
            "audience_list_id", "report_id", "format");

    public static final ErrorCode<AudienceListRestException> SNAPSHOT_NOT_SUPPORTED =
        new ErrorCode<>("audience_list_snapshot_not_supported", 400,
            "Unable to snapshot audience list that is not READY", "audience_list_id");

    public AudienceListRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
