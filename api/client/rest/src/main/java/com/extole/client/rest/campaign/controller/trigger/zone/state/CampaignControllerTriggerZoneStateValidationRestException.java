package com.extole.client.rest.campaign.controller.trigger.zone.state;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerZoneStateValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerZoneStateValidationRestException> ZONE_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_zone_state_zone_name_expression_out_of_range", 400,
                "Zone name expression length is invalid", "expression", "max_length");

    public static final ErrorCode<
        CampaignControllerTriggerZoneStateValidationRestException> STEP_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_zone_state_step_name_expression_out_of_range", 400,
                "Step name expression length is invalid", "expression", "max_length");

    public CampaignControllerTriggerZoneStateValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
