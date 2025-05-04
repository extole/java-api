package com.extole.client.rest.campaign.controller.trigger;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerValidationRestException> INVALID_NAME_LENGTH =
        new ErrorCode<>("campaign_controller_trigger_name_length", 400,
            "Invalid name length", "name", "max_length");

    public static final ErrorCode<CampaignControllerTriggerValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_controller_trigger_description_length_out_of_range", 400,
            "Campaign controller trigger description length is out of range", "description", "max_length");

    public static final ErrorCode<CampaignControllerTriggerValidationRestException> MISSING_NAME =
        new ErrorCode<>("campaign_controller_trigger_name_invalid", 400,
            "Invalid name, trigger name can't be empty");

    public static final ErrorCode<CampaignControllerTriggerValidationRestException> TRIGGER_TYPE_NOT_SUPPORTED =
        new ErrorCode<>("trigger_type_not_supported", 400, "Trigger type is not supported", "supported_trigger_types",
            "step_type");

    public CampaignControllerTriggerValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
