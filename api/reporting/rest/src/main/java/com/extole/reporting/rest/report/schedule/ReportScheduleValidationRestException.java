package com.extole.reporting.rest.report.schedule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportScheduleValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ReportScheduleValidationRestException> MISSING_NAME =
        new ErrorCode<>("report_schedule_name_missing", 400, "Report schedule name is missing");

    public static final ErrorCode<ReportScheduleValidationRestException> MISSING_REPORT_TYPE =
        new ErrorCode<>("report_schedule_report_type_missing", 400, "Report schedule report type is missing");

    public static final ErrorCode<ReportScheduleValidationRestException> MISSING_START_DATE =
        new ErrorCode<>("report_schedule_start_date_missing", 400, "Report schedule start date is missing");

    public static final ErrorCode<ReportScheduleValidationRestException> MISSING_FREQUENCY =
        new ErrorCode<>("report_schedule_frequency_missing", 400, "Report schedule frequency is missing");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_TYPE_NOT_FOUND =
        new ErrorCode<>("report_schedule_type_not_found", 400, "Report type not found", "name");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_MISSING_PARAMETERS =
        new ErrorCode<>("report_schedule_missing_parameters", 400, "Report is missing parameters", "parameters");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_INVALID_PARAMETER =
        new ErrorCode<>(
            "report_schedule_invalid_parameter", 400, "Report parameter(s) of invalid format", "parameters");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_INVALID_FORMATS = new ErrorCode<>(
        "report_schedule_invalid_formats", 400, "Report Schedule formats not supported", "formats");
    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_MERGE_EMPTY_FORMATS = new ErrorCode<>(
        "report_schedule_merge_empty_formats", 400, "Report Schedule empty merge formats");

    public static final ErrorCode<
        ReportScheduleValidationRestException> REPORT_SCHEDULE_FREQUENCY_NOT_SUPPORTED_FOR_LEGACY_SFTP =
            new ErrorCode<>("report_schedule_frequency_not_supported_for_legacy_sftp", 400,
                "Report Schedule frequency is not supported for legacy SFTP name format");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_INVALID_SCOPES =
        new ErrorCode<>("report_schedule_invalid_scopes", 400, "Report schedule scopes invalid");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_INVALID_SFTP_SERVER =
        new ErrorCode<>("report_schedule_invalid_sftp_server", 400, "Invalid SFTP server", "sftp_server_id");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_INVALID_SFTP_KEY_MISSING =
        new ErrorCode<>("report_schedule_invalid_sftp_key_missing", 400, "Client's SFTP key is missing");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_LOCKED = new ErrorCode<>(
        "report_schedule_locked", 400, "Report Schedule is locked and cannot be edited via rest api");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("report_schedule_name_illegal_character", 403,
            "Report Schedule name can only contain alphanumeric, space, dash, parenthesis, slash, " +
                "colon, comma, period, underscore, dollar and percentage",
            "name");

    public static final ErrorCode<ReportScheduleValidationRestException> REPORT_SCHEDULE_INVALID_SORT_BY =
        new ErrorCode<>("report_schedule_invalid_sort_by", 400,
            "Invalid sort by value defined by report merging configuration", "sort_by");

    public ReportScheduleValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
