package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignScheduleValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignScheduleValidationRestException> REMOVE_START_DATE =
        new ErrorCode<>("remove_start_date", 400, "Cannot remove start date for already started campaign");

    public static final ErrorCode<CampaignScheduleValidationRestException> BACKDATED_START_DATE =
        new ErrorCode<>("backdated_start_date", 400, "Cannot set backdated start date", "start_date");

    public static final ErrorCode<CampaignScheduleValidationRestException> BACKDATED_STOP_DATE =
        new ErrorCode<>("backdated_stop_date", 400, "Cannot set backdated stop date", "stop_date");

    public static final ErrorCode<CampaignScheduleValidationRestException> START_DATE_AFTER_STOP_DATE =
        new ErrorCode<>("start_date_after_stop_date", 400, "Start date is after stop date", "start_date", "stop_date");

    public static final ErrorCode<CampaignScheduleValidationRestException> DATE_BEFORE_START_DATE = new ErrorCode<>(
        "date_before_start_date", 400, "Date is before scheduled campaign start date", "date", "start_date");

    public static final ErrorCode<CampaignScheduleValidationRestException> DATE_AFTER_STOP_DATE = new ErrorCode<>(
        "date_after_stop_date", 400, "Date is after scheduled campaign stop date", "date", "stop_date");

    public static final ErrorCode<CampaignScheduleValidationRestException> SIBLING_CAMPAIGN_SCHEDULED = new ErrorCode<>(
        "sibling_campaign_scheduled", 400, "Another campaign with same program label is already scheduled to start",
        "sibling_campaign_id", "sibling_campaign_name", "sibling_campaign_start_date");

    public CampaignScheduleValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
