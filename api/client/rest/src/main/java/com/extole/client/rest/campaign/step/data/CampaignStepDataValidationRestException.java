package com.extole.client.rest.campaign.step.data;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignStepDataValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignStepDataValidationRestException> MISSING_DATA_NAME =
        new ErrorCode<>("campaign_step_missing_data_name", 400, "Data name is missing");

    public static final ErrorCode<CampaignStepDataValidationRestException> DATA_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_step_data_name_length_out_of_range", 400,
            "Data name length is out of range. Max 255 characters", "name");

    public static final ErrorCode<CampaignStepDataValidationRestException> MISSING_DATA_VALUE =
        new ErrorCode<>("campaign_step_missing_data_value", 400, "Data value is missing", "name");

    public static final ErrorCode<CampaignStepDataValidationRestException> DATA_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_step_data_value_expression_length_out_of_range", 400,
            "Data value expression length is out of range. Max 2000 characters", "expression", "name");

    public static final ErrorCode<CampaignStepDataValidationRestException>
        DATA_DEFAULT_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_step_data_default_value_expression_length_out_of_range", 400,
            "Data default value expression length is out of range. Max 2000 characters", "expression", "name");

    public static final ErrorCode<CampaignStepDataValidationRestException> DUPLICATE_DATA_NAME =
        new ErrorCode<>("campaign_step_duplicate_data_name", 400, "Data name is duplicated", "name");

    public static final ErrorCode<CampaignStepDataValidationRestException> LEGACY_ACTION_ID_STEP_DATA_DELETION =
        new ErrorCode<>("campaign_controller_data_legacy_action_id_step_data_deletion", 400,
            "Legacy action id step data deletion is not allowed if a controller has an incentivize action",
            "controller_id");

    public static final ErrorCode<CampaignStepDataValidationRestException> MULTIPLE_KEYS_NOT_ALLOWED =
        new ErrorCode<>("multiple_keys_not_allowed", 400, "Journey is allowed to have just one key", "data_names");

    public CampaignStepDataValidationRestException(String uniqueId,
        ErrorCode<CampaignStepDataValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
