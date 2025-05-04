package com.extole.reporting.rest.report.execution;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportValidationRestException extends ExtoleRestException {
    public static final ErrorCode<ReportValidationRestException> MISSING_TYPE =
        new ErrorCode<>("report_type_missing", 400, "Report type is missing");

    public static final ErrorCode<ReportValidationRestException> DISPLAY_NAME_EMPTY =
        new ErrorCode<>("report_display_name_empty", 400, "Report display name is empty");

    public static final ErrorCode<ReportValidationRestException> DISPLAY_NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("report_name_illegal_character", 400,
            "Report name can only contain alphanumeric, space, dash, parenthesis, slash, " +
                "colon, comma, period, underscore, dollar and percentage",
            "display_name");

    public static final ErrorCode<ReportValidationRestException> DISPLAY_NAME_TOO_LONG =
        new ErrorCode<>("report_display_name_too_long", 400, "Report display name is too long");

    public static final ErrorCode<ReportValidationRestException> TAGS_TOO_LONG =
        new ErrorCode<>("report_tags_too_long", 400, "Tags are too long", "tags");

    public static final ErrorCode<ReportValidationRestException> REPORT_MISSING_PARAMETERS =
        new ErrorCode<>("report_missing_parameters", 400, "Report is missing parameters", "parameters");

    public static final ErrorCode<ReportValidationRestException> REPORT_INVALID_OPERATION = new ErrorCode<>(
        "report_invalid_operation", 400, "Current report state doesn't allow the specified operation", "report_id");

    public static final ErrorCode<ReportValidationRestException> REPORT_INVALID_PARAMETER = new ErrorCode<>(
        "report_invalid_parameter", 400, "Report parameter(s) of invalid format", "parameters");

    public static final ErrorCode<ReportValidationRestException> REPORT_INVALID_FORMATS = new ErrorCode<>(
        "report_invalid_formats", 400, "Report formats not supported", "formats");

    public static final ErrorCode<ReportValidationRestException> REPORT_INVALID_SCOPES = new ErrorCode<>(
        "report_invalid_scopes", 400, "Report scopes invalid");

    public static final ErrorCode<ReportValidationRestException> REPORT_INVALID_SFTP_KEY_MISSING = new ErrorCode<>(
        "report_invalid_sftp_key_missing", 400, "Client's SFTP key is missing");

    public static final ErrorCode<ReportValidationRestException> REPORT_INVALID_SFTP_SERVER = new ErrorCode<>(
        "report_invalid_sftp_server", 400, "Invalid SFTP server", "sftp_server_id");

    public ReportValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
