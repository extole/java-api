package com.extole.reporting.rest.demo.data;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class DemoDataRestException extends ExtoleRestException {

    public static final ErrorCode<DemoDataRestException> MISSING_DATE = new ErrorCode<>(
        "demo_data_date_missing", 400, "startDate and endDate are required");

    public static final ErrorCode<DemoDataRestException> BATCH_UPLOAD_FAILED = new ErrorCode<>(
        "demo_data_batch_upload_file", 400, "Failed to upload plan");

    public static final ErrorCode<DemoDataRestException> MISSING_EVENTS_PER_DAY = new ErrorCode<>(
        "demo_data_events_per_day_missing", 400, "eventsPerDay is required");

    public static final ErrorCode<DemoDataRestException> NEGATIVE_PROBABILITY = new ErrorCode<>(
        "demo_data_negative_probability", 400, "Zero and negative probabilities are not allowed",
        "details_message");

    public static final ErrorCode<DemoDataRestException> INVALID_EVENTS_PER_DAY = new ErrorCode<>(
        "demo_data_events_per_day_invalid_value", 400, "A limit amount of events per day are allowed",
        "max_events_per_day");

    public static final ErrorCode<DemoDataRestException> INVALID_PERSON_SIDE = new ErrorCode<>(
        "demo_data_person_side_invalid", 400, "Person side is required",
        "event_name");

    public static final ErrorCode<DemoDataRestException> MISSING_FLOWS = new ErrorCode<>(
        "demo_data_empty_flows", 400, "DemData can't run without flows");

    public static final ErrorCode<DemoDataRestException> INVALID_PROGRAM_LABEL = new ErrorCode<>(
        "demo_data_program_label_missing", 400, "programLabel is required");

    public static final ErrorCode<DemoDataRestException> INVALID_DATE_INTERVAL = new ErrorCode<>(
        "demo_data_date_interval_missing", 400, "startDate should be before endDate");

    public DemoDataRestException(String uniqueId, ErrorCode<DemoDataRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
