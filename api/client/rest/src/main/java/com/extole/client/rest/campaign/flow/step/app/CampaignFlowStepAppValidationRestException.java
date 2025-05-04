package com.extole.client.rest.campaign.flow.step.app;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignFlowStepAppValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignFlowStepAppValidationRestException> APP_NAME_MISSING =
        new ErrorCode<>("campaign_flow_step_app_name_missing", 400, "App name is missing");

    public static final ErrorCode<CampaignFlowStepAppValidationRestException> APP_TYPE_MISSING =
        new ErrorCode<>("campaign_flow_step_app_type_missing", 400, "App type is missing");

    public static final ErrorCode<CampaignFlowStepAppValidationRestException> APP_TYPE_NAME_MISSING =
        new ErrorCode<>("campaign_flow_step_app_type_name_missing", 400, "App type name is missing");

    public static final ErrorCode<CampaignFlowStepAppValidationRestException> APP_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_app_name_length_out_of_range", 400,
            "App name length has to be between 1 and 255", "app_name", "min_length", "max_length");

    public static final ErrorCode<CampaignFlowStepAppValidationRestException> APP_TYPE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_app_type_name_length_out_of_range", 400,
            "App type name length has to be between 1 and 255", "app_type_name", "min_length", "max_length");

    public static final ErrorCode<CampaignFlowStepAppValidationRestException> APP_DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_app_description_length_out_of_range", 400,
            "App description length has to be between 1 and 2000", "app_description", "max_length");

    public CampaignFlowStepAppValidationRestException(String uniqueId, ErrorCode<?> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
