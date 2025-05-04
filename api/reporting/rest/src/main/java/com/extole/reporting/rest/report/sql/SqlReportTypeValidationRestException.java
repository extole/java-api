package com.extole.reporting.rest.report.sql;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;

public class SqlReportTypeValidationRestException extends ReportTypeValidationRestException {

    public static final ErrorCode<SqlReportTypeValidationRestException> MISSING_DATABASE =
        new ErrorCode<>("sql_report_type_database_missing", 400, "SQL report type database is missing");
    public static final ErrorCode<SqlReportTypeValidationRestException> MISSING_QUERY =
        new ErrorCode<>("sql_report_type_query_missing", 400, "SQL report type query is missing");
    public static final ErrorCode<SqlReportTypeValidationRestException> VISIBILITY_INVALID =
        new ErrorCode<>("sql_report_type_visibility_invalid", 400, "SQL report type visibility could not be changed");
    public static final ErrorCode<SqlReportTypeValidationRestException> QUERY_TOO_LONG =
        new ErrorCode<>("sql_report_type_query_too_long", 400, "SQL report type query is too long");
    public static final ErrorCode<SqlReportTypeValidationRestException> NAME_TOO_LONG =
        new ErrorCode<>("sql_report_type_name_too_long", 400, "SQL report type name is too long");
    public static final ErrorCode<SqlReportTypeValidationRestException> NAME_INVALID =
        new ErrorCode<>("sql_report_type_name_invalid", 400,
            "SQL report type name should contain alphanumeric, underscore and dash characters only", "name");
    public static final ErrorCode<SqlReportTypeValidationRestException> NAME_DUPLICATED =
        new ErrorCode<>("sql_report_type_name_duplicated", 400, "SQL report type name duplicated", "name");
    public static final ErrorCode<SqlReportTypeValidationRestException> INVALID_ALLOWED_SCOPES = new ErrorCode<>(
        "sql_report_type_invalid_allowed_scopes", 400, "SQL report type allowed scopes invalid");
    public static final ErrorCode<SqlReportTypeValidationRestException> DISPLAY_NAME_TOO_LONG =
        new ErrorCode<>("sql_report_type_display_name_too_long", 400, "SQL report type display name is too long");
    public static final ErrorCode<SqlReportTypeValidationRestException> DESCRIPTION_TOO_LONG =
        new ErrorCode<>("sql_report_type_description_too_long", 400, "SQL report type description is too long");
    public static final ErrorCode<SqlReportTypeValidationRestException> INVALID_DESCRIPTION_LINK =
        new ErrorCode<>("report_type_invalid_description_link", 400, "Report type description link is invalid");
    public static final ErrorCode<SqlReportTypeValidationRestException> INVALID_UPDATE = new ErrorCode<>(
        "report_type_update_invalid", 400, "Report type not allowed to be updated");
    public static final ErrorCode<SqlReportTypeValidationRestException> DISPLAY_NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("sql_report_type_display_name_illegal_character", 403,
            "SQL report type display name can only contain alphanumeric, space, dash, parenthesis, slash, " +
                "colon, comma, period, underscore, dollar and percentage",
            "name");

    public SqlReportTypeValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
