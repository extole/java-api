package com.extole.reporting.rest.dashboard.summary;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportingRestException extends ExtoleRestException {

    public static final ErrorCode<ReportingRestException> REWARD_SUMMARY_ERROR =
        new ErrorCode<>("reward_summary_error", 400,
            "Error retrieving reward events summary.", "client_id", "start_date", "end_date", "reward_type");
    public static final ErrorCode<ReportingRestException> REWARD_SUMMARY_INTERVAL_ERROR =
        new ErrorCode<>("reward_summary_interval_error", 400,
            "Invalid reporting interval.", "client_id", "start_date", "end_date");
    public static final ErrorCode<ReportingRestException> REWARD_INVALID_TYPE_ERROR =
        new ErrorCode<>("reward_invalid_type_error", 400,
            "Invalid reward type value. Expecting one of coupon, account_credit, manual_coupon,"
                + " salesforce_coupon, tango_v2, custom_reward",
            "reward_type");

    public ReportingRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
