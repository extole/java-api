package com.extole.reporting.rest.report.runner;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportRunnerValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_MISSING_NAME =
        new ErrorCode<>("report_runner_name_missing", 400, "Report runner name is missing");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_MISSING_REPORT_TYPE =
        new ErrorCode<>("report_runner_type_field_missing", 400, "Report runner request is missing type field");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_MISSING_START_DATE =
        new ErrorCode<>("report_runner_start_date_missing", 400, "Report runner start date is missing");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_MISSING_FREQUENCY =
        new ErrorCode<>("report_runner_frequency_missing", 400, "Report runner frequency is missing");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_TYPE_NOT_FOUND =
        new ErrorCode<>("report_runner_type_not_found", 400, "Report runner type not found", "type");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_MISSING_PARAMETERS =
        new ErrorCode<>("report_runner_missing_parameters", 400, "Report runner is missing parameters", "parameters");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_INVALID_PARAMETER =
        new ErrorCode<>(
            "report_runner_invalid_parameter", 400, "Report runner parameter(s) of invalid format", "parameters");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_INVALID_FORMATS = new ErrorCode<>(
        "report_runner_invalid_formats", 400, "Report runner formats not supported", "formats");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_MERGE_EMPTY_FORMATS =
        new ErrorCode<>(
            "report_runner_merge_empty_formats", 400, "Report runner merge empty formats");

    public static final ErrorCode<
        ReportRunnerValidationRestException> REPORT_RUNNER_FREQUENCY_NOT_SUPPORTED_FOR_LEGACY_SFTP =
            new ErrorCode<>("report_runner_frequency_not_supported_for_legacy_sftp", 400,
                "Report runner frequency is not supported for legacy SFTP name format");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_INVALID_SCOPES =
        new ErrorCode<>("report_runner_invalid_scopes", 400, "Report runner scopes invalid");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_INVALID_SFTP_SERVER =
        new ErrorCode<>("report_runner_invalid_sftp_server", 400, "Invalid SFTP server", "sftp_server_id");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_NAME_EXISTS =
        new ErrorCode<>("report_runner_name_exists", 400, "Report runner name already exists", "name");
    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("report_runner_name_illegal_character", 400,
            "Report Runner name can only contain alphanumeric, space, dash, parenthesis, slash, " +
                "colon, comma, period, underscore, dollar and percentage",
            "name");
    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_INVALID_SORT_BY =
        new ErrorCode<>("report_runner_invalid_sort_by", 400,
            "Invalid sort by value defined by report merging configuration", "sort_by");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_LOCKED = new ErrorCode<>(
        "report_runner_locked", 400, "Report runner is locked and cannot be edited via rest api");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_NO_EXECUTION_TIME_RANGE =
        new ErrorCode<>(
            "report_runner_no_execution_time_range", 400, "Report runner has no valid execution time ranges to run",
            "report_runner_id");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_PAUSED =
        new ErrorCode<>("report_runner_paused", 400, "Report runner is paused",
            "report_runner_id");
    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_DUPLICATE =
        new ErrorCode<>("report_runner_duplicate", 400, "Report runner has duplicate", "report_runner_ids");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_INVALID_SLOT =
        new ErrorCode<>(
            "report_runner_invalid_slot", 400, "Report runner slot of invalid format", "slot");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_SLOTS_NOT_SUPPORTED =
        new ErrorCode<>("report_runner_slots_not_supported", 400, "Report runner slots is not supported");

    public static final ErrorCode<ReportRunnerValidationRestException> REPORT_RUNNER_WRONG_TYPE =
        new ErrorCode<>("report_runner_wrong_type", 400, "Report Runner of wrong type");

    public ReportRunnerValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
