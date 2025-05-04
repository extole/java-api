package com.extole.reporting.rest.report.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportTypeValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ReportTypeValidationRestException> VISIBILITY_INVALID =
        new ErrorCode<>("report_type_visibility_invalid", 400, "Report type visibility can not be changed");
    public static final ErrorCode<ReportTypeValidationRestException> INVALID_ALLOWED_SCOPES = new ErrorCode<>(
        "report_type_invalid_allowed_scopes", 400, "Report type allowed scopes invalid");
    public static final ErrorCode<ReportTypeValidationRestException> DISPLAY_NAME_TOO_LONG =
        new ErrorCode<>("report_type_display_name_too_long", 400, "Report type display name is too long");
    public static final ErrorCode<ReportTypeValidationRestException> DISPLAY_NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("report_type_display_name_illegal_character", 403,
            "Report type display name can only contain alphanumeric, space, dash, parenthesis, slash, " +
                "colon, comma, period, underscore, dollar and percentage",
            "name");
    public static final ErrorCode<ReportTypeValidationRestException> DESCRIPTION_TOO_LONG =
        new ErrorCode<>("report_type_description_too_long", 400, "Report type description is too long");
    public static final ErrorCode<ReportTypeValidationRestException> INVALID_DESCRIPTION_LINK =
        new ErrorCode<>("report_type_invalid_description_link", 400, "Report type description link is invalid");
    public static final ErrorCode<ReportTypeValidationRestException> CLIENTS_INVALID =
        new ErrorCode<>("report_type_change_clients_invalid", 400,
            "Report type clients can not be changed for not PRIVATE report type");
    public static final ErrorCode<ReportTypeValidationRestException> EMPTY_TAG_NAME =
        new ErrorCode<>("report_type_empty_tag_name", 400, "Report type tags can not have null names");

    public static final ErrorCode<ReportTypeValidationRestException> EMPTY_PARAMETER_NAME =
        new ErrorCode<>("report_type_empty_parameter_name", 400, "Report type parameters can not have null names");
    public static final ErrorCode<ReportTypeValidationRestException> PARAMETER_DESCRIPTION_TOO_LONG =
        new ErrorCode<>("report_type_parameter_description_too_long", 400,
            "Report type parameter description are too long");

    public static final ErrorCode<ReportTypeValidationRestException> PARAMETER_STATIC_ADD =
        new ErrorCode<>("report_type_parameter_static_add", 400, "Report type could not have new static parameters",
            "parameters");

    public static final ErrorCode<ReportTypeValidationRestException> PARAMETER_STATIC_UPDATE =
        new ErrorCode<>("report_type_parameter_static_change", 400,
            "Report type could not change type for static parameters", "parameters");

    public static final ErrorCode<ReportTypeValidationRestException> PARAMETER_STATIC_DELETE =
        new ErrorCode<>("report_type_parameter_static_delete", 400, "Report type could not delete static parameters",
            "parameters");
    public static final ErrorCode<ReportTypeValidationRestException> INVALID_UPDATE = new ErrorCode<>(
        "report_type_update_invalid", 400, "Report type not allowed to be updated");

    public ReportTypeValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
