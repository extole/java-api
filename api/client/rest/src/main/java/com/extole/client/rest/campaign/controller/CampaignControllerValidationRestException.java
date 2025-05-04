package com.extole.client.rest.campaign.controller;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignControllerValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignControllerValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_controller_name_out_of_range", 400,
            "Campaign controller name length must be between 2 and 255 characters", "name");

    public static final ErrorCode<CampaignControllerValidationRestException> DUPLICATE_CONTROLLER =
        new ErrorCode<>("campaign_controller_duplicate_controller", 400,
            "Two controllers have the same unique key (controller name, journey name)",
            "controller_name", "first_controller_id", "first_controller_journey_names", "second_controller_id",
            "second_controller_journey_names");

    public static final ErrorCode<CampaignControllerValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_controller_name_contains_illegal_character", 400,
            "Campaign controller name can only contain alphanumeric, dash and underscore characters", "name");

    public static final ErrorCode<CampaignControllerValidationRestException> NAME_MISSING =
        new ErrorCode<>("campaign_controller_name_missing", 400, "Campaign controller name is missing");

    public static final ErrorCode<CampaignControllerValidationRestException> INVALID_ENABLED_ON_STATE =
        new ErrorCode<>("campaign_controller_invalid_enabled_on_state", 400, "Invalid controller enabled on state",
            "enabled_on_states");

    public static final ErrorCode<CampaignControllerValidationRestException> INVALID_STEP_ALIAS = new ErrorCode<>(
        "invalid_step_alias", 400, "Invalid step alias", "alias");

    public static final ErrorCode<CampaignControllerValidationRestException> NAME_SAME_AS_A_DEFAULT_CONTROLLER_NAME =
        new ErrorCode<>("campaign_controller_name_same_as_a_default_controller_name", 400,
            "Campaign controller name is the same as a default controller name", "name");

    public static final ErrorCode<CampaignControllerValidationRestException> CONTROLLER_MISSING_EVENT_TRIGGER =
        new ErrorCode<>("campaign_controller_missing_event_trigger", 400,
            "A controller without enabled matching event trigger is invalid", "controller_id", "campaign_id");

    public static final ErrorCode<CampaignControllerValidationRestException> MISSING_JOURNEY_NAME =
        new ErrorCode<>("campaign_controller_missing_journey_name", 400, "Missing journey name");

    public static final ErrorCode<CampaignControllerValidationRestException> TOO_MANY_JOURNEY_NAMES =
        new ErrorCode<>("campaign_controller_too_many_journey_names", 400, "Too many journey names", "journey_names",
            "actual_count", "max_count");

    public CampaignControllerValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
