package com.extole.client.rest.campaign.controller.action.incentivize;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionIncentivizeValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionIncentivizeValidationRestException> OVERRIDE_VALUE_INVALID =
        new ErrorCode<>("campaign_controller_action_incentivize_override_value_invalid", 400,
            "Override value is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeValidationRestException> OVERRIDE_VALUE_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_incentivize_override_value_length_out_of_range", 400,
                "Override value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeValidationRestException> INCENTIVIZE_ACTION_TYPE_MISSING =
            new ErrorCode<>("campaign_controller_action_incentivize_action_type_missing", 400,
                "Incentivize action type is required");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeValidationRestException> INCENTIVIZE_ACTION_NAME_INVALID_VALUE =
            new ErrorCode<>("campaign_controller_action_incentivize_action_name_invalid_value", 400,
                "Incentivize action name can't be empty");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeValidationRestException> INCENTIVIZE_ACTION_NAME_INVALID_LENGTH =
            new ErrorCode<>("campaign_controller_action_incentivize_action_name_invalid_length", 400,
                "Incentivize action name can't be greater that 2000 characters");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeValidationRestException> INCENTIVIZE_ACTION_NAME_INVALID_EXPRESSION =
            new ErrorCode<>("campaign_controller_action_incentivize_action_name_invalid_expression", 400,
                "Incentivize action name is not a valid SpEL expression");

    public static final ErrorCode<CampaignControllerActionIncentivizeValidationRestException> DATA_NAME_INVALID =
        new ErrorCode<>("data_name_invalid", 400, "Data name is invalid");

    public static final ErrorCode<CampaignControllerActionIncentivizeValidationRestException> DATA_NAME_LENGTH_INVALID =
        new ErrorCode<>("data_name_length_invalid", 400,
            "Data name can't be blank or longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionIncentivizeValidationRestException> DATA_VALUE_INVALID =
        new ErrorCode<>("data_value_invalid", 400, "Data value is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeValidationRestException> DATA_VALUE_LENGTH_INVALID =
            new ErrorCode<>("data_value_length_invalid", 400,
                "Data value can't be blank or longer than maximum length", "name", "max_length");

    public CampaignControllerActionIncentivizeValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionIncentivizeValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
