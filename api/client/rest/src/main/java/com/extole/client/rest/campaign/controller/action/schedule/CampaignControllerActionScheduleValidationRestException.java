package com.extole.client.rest.campaign.controller.action.schedule;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionScheduleValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> MISSING_SCHEDULE_NAME =
        new ErrorCode<>("campaign_controller_action_schedule_missing_schedule_name", 400, "Missing schedule name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> SCHEDULE_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_schedule_name_out_of_range", 400,
                "Schedule name length must be between 2 and 100 characters", "schedule_name",
                "evaluated_schedule_name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> SCHEDULE_NAME_CONTAINS_ILLEGAL_CHARACTER =
            new ErrorCode<>(
                "campaign_controller_action_schedule_name_contains_illegal_character", 400,
                "Schedule name can only contain alphanumeric, dash and underscore characters", "schedule_name");

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> NEGATIVE_SCHEDULE_DELAY =
        new ErrorCode<>("campaign_controller_action_schedule_negative_delay", 400,
            "Schedule delay must be positive number", "delay");

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> INVALID_SCHEDULE_DELAY =
        new ErrorCode<>("campaign_controller_action_schedule_invalid_delay", 400,
            "Schedule delays is null or contains a null delay");

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> INVALID_SCHEDULE_DATE =
        new ErrorCode<>("campaign_controller_action_schedule_invalid_date", 400,
            "Invalid schedule date (must be date in ISO-8601 format)", "date");

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> BACKDATED_DATE =
        new ErrorCode<>("campaign_controller_action_schedule_backdated_date_not_allowed", 400,
            "Backdated dates are not allowed", "date");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> DATA_ENTRY_NAME_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_schedule_data_entry_name_out_of_range", 400,
                "Data entry name cannot be blank or exceed the maximum allowed length of 255 characters",
                "data_entry_name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> DELAYS_AND_DATES_NOT_SUPPORTED_TOGETHER =
            new ErrorCode<>(
                "campaign_controller_action_schedule_delays_and_dates_not_supported_together", 400,
                "It is not allowed to configure both, date and delay and the same time");

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> DUPLICATE_DATA_ENTRY_NAME =
        new ErrorCode<>("campaign_controller_action_schedule_duplicate_data_entry_name",
            400, "Two data entry names evaluated to the same value", "evaluated_data_entry_name",
            "first_data_entry_name_evaluatable", "second_data_entry_name_evaluatable");

    public CampaignControllerActionScheduleValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionScheduleValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
