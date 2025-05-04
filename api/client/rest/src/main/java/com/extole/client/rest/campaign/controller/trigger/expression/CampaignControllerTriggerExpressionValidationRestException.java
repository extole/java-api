package com.extole.client.rest.campaign.controller.trigger.expression;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerExpressionValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerExpressionValidationRestException> DATA_MISSING =
        new ErrorCode<>("campaign_controller_trigger_expression_no_data", 400, "Missing expressions");

    public static final ErrorCode<CampaignControllerTriggerExpressionValidationRestException> EXPRESSION_MISSING =
        new ErrorCode<>("campaign_controller_trigger_expression_data_null", 400, "Invalid expression", "data");

    public static final ErrorCode<CampaignControllerTriggerExpressionValidationRestException> INVALID_EXPRESSION =
        new ErrorCode<>("campaign_controller_trigger_expression_invalid_expression", 400,
            "Invalid expression", "expression");

    public static final ErrorCode<
        CampaignControllerTriggerExpressionValidationRestException> EXPRESSION_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_expression_data_out_of_range", 400, "Invalid expression",
                "data");

    public CampaignControllerTriggerExpressionValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
