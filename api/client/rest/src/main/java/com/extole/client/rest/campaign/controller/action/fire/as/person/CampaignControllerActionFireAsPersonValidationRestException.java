package com.extole.client.rest.campaign.controller.action.fire.as.person;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionFireAsPersonValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> AS_PERSON_IDENTIFICATION_MISSING =
            new ErrorCode<>("campaign_controller_action_fire_as_person_identification_missing", 400,
                "Person identification is required.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_action_fire_as_person_identification_value_length_out_of_range",
                400, "Person identification value is out of range. Maximum length is 2000 characters.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> AS_PERSON_JOURNEY_FIELD_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_action_fire_as_person_journey_field_out_of_range",
                400, "A person journey field value is out of range. Maximum length is 1000 characters.", "field_name");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> AS_PERSON_JOURNEY_FIELD_INVALID =
            new ErrorCode<>("campaign_controller_action_fire_as_person_journey_field_invalid",
                400, "A person journey field value is invalid", "field_name");

    public static final ErrorCode<CampaignControllerActionFireAsPersonValidationRestException> DATA_VALUE_INVALID =
        new ErrorCode<>("campaign_controller_action_fire_as_person_data_value_invalid",
            400, "A data value is invalid.", "data_name");

    public static final ErrorCode<CampaignControllerActionFireAsPersonValidationRestException> DATA_NAME_OUT_OF_RANGE =
        new ErrorCode<>("campaign_controller_action_fire_as_person_data_name_out_of_range",
            400, "A data name is out of range. Maximum length is 200 characters.", "data_name");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> DUPLICATE_DATA_ENTRY_NAME =
            new ErrorCode<>("campaign_controller_action_fire_as_person_duplicate_data_entry_name",
                400, "Two data entry names evaluated to the same value", "evaluated_data_entry_name",
                "first_data_entry_name_evaluatable", "second_data_entry_name_evaluatable");

    public static final ErrorCode<CampaignControllerActionFireAsPersonValidationRestException> MISSING_EVENT_NAME =
        new ErrorCode<>("campaign_controller_action_fire_as_person_missing_event_name",
            400, "Event name is required.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> EVENT_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_action_fire_as_person_event_name_length_out_of_range",
                400, "Event name is not of valid length", "event_name");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER =
            new ErrorCode<>("campaign_controller_action_fire_as_person_event_name_contains_illegal_character",
                400, "Event name should contain alphanumeric, underscore, dash, dot and space characters only",
                "event_name");

    public static final ErrorCode<CampaignControllerActionFireAsPersonValidationRestException> LABEL_OUT_OF_RANGE =
        new ErrorCode<>("campaign_controller_action_fire_as_person_label_out_of_range",
            400, "Label length is out of range. Allowed range is between 2 and 50.", "label");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> LABEL_ILLEGAL_CHARACTERS =
            new ErrorCode<>("campaign_controller_action_fire_as_person_label_illegal_characters",
                400, "Label contains illegal characters."
                    + " Only lowercase alphabetic, numeric, underscore and dash characters are allowed.",
                "label");

    public static final ErrorCode<CampaignControllerActionFireAsPersonValidationRestException> JOURNEY_NAME_MISSING =
        new ErrorCode<>("campaign_controller_action_fire_as_person_journey_name_missing",
            400, "Journey name is required.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> JOURNEY_MISSING_REFERRAL_DATA =
            new ErrorCode<>("campaign_controller_action_fire_as_person_journey_missing_referral_data",
                400, "Journey data is missing for referral reason.", "data_name");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonValidationRestException> DOUBLE_PERSON_IDENTIFICATION =
            new ErrorCode<>("campaign_controller_action_fire_as_person_double_person_identification", 400,
                "Please provide just one form of person identification.");

    public CampaignControllerActionFireAsPersonValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionFireAsPersonValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
