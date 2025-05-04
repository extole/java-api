package com.extole.client.rest.campaign.controller.action.step.signal;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionStepSignalValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionStepSignalValidationRestException> POLLING_ID_INVALID_LENGTH =
        new ErrorCode<>("campaign_controller_action_step_signal_polling_id_invalid_length", 400,
            "Step signal polling id value length is out of range. Max 2000 chars", "polling_id");

    public static final ErrorCode<
        CampaignControllerActionStepSignalValidationRestException> POLLING_ID_INVALID_EXPRESSION =
            new ErrorCode<>("campaign_controller_action_step_signal_polling_id_invalid_expression", 400,
                "Invalid polling id expression", "polling_id");

    public static final ErrorCode<CampaignControllerActionStepSignalValidationRestException> NAME_INVALID_LENGTH =
        new ErrorCode<>("campaign_controller_action_step_signal_name_invalid_length", 400,
            "Step signal name value length invalid. Max 255 chars", "name");

    public CampaignControllerActionStepSignalValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionStepSignalValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
