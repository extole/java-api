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

    public static final ErrorCode<CampaignControllerActionScheduleValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("campaign_controller_action_schedule_data_attribute_name_invalid", 400,
            "Data attribute name is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_schedule_data_attribute_name_length_out_of_range", 400,
                "Data attribute name length is out of range. Max 200 chars", "name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
            new ErrorCode<>(
                "campaign_controller_action_schedule_data_attribute_value_invalid", 400,
                "Data attribute value is invalid",
                "name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_schedule_data_attribute_value_length_out_of_range", 400,
                "Data attribute value length is out of range. Max 2048 chars", "name");

    public static final ErrorCode<
        CampaignControllerActionScheduleValidationRestException> DELAYS_AND_DATES_NOT_SUPPORTED_TOGETHER =
            new ErrorCode<>(
                "campaign_controller_action_schedule_delays_and_dates_not_supported_together", 400,
                "It is not allowed to configure both, date and delay and the same time");

    public CampaignControllerActionScheduleValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionScheduleValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
