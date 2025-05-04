package com.extole.client.rest.campaign.flow.step;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignFlowStepValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignFlowStepValidationRestException> FLOW_PATH_MISSING =
        new ErrorCode<>("campaign_flow_step_flow_path_missing", 400, "Flow path is missing");

    public static final ErrorCode<CampaignFlowStepValidationRestException> SEQUENCE_MISSING =
        new ErrorCode<>("campaign_flow_step_sequence_missing", 400, "Sequence is missing");

    public static final ErrorCode<CampaignFlowStepValidationRestException> STEP_NAME_MISSING =
        new ErrorCode<>("campaign_flow_step_step_name_missing", 400, "Step name is missing");

    public static final ErrorCode<CampaignFlowStepValidationRestException> ICON_TYPE_MISSING =
        new ErrorCode<>("campaign_flow_step_icon_type_missing", 400, "Icon type is missing");

    public static final ErrorCode<CampaignFlowStepValidationRestException> FLOW_PATH_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_flow_path_length_out_of_range", 400,
            "Flow path length has to be between 2 and 255", "flow_path");

    public static final ErrorCode<CampaignFlowStepValidationRestException> FLOW_PATH_INVALID =
        new ErrorCode<>("campaign_flow_step_flow_path_invalid", 400,
            "Flow path has to match pattern /main_flow/sub_flow_1/sub_flow_2 etc", "flow_path");

    public static final ErrorCode<CampaignFlowStepValidationRestException> STEP_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_step_name_length_out_of_range", 400,
            "Step name length has to be between 1 and 255", "step_name");

    public static final ErrorCode<CampaignFlowStepValidationRestException> ICON_TYPE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_icon_type_length_out_of_range", 400,
            "Icon type length has to be between 1 and 64", "icon_type");

    public static final ErrorCode<CampaignFlowStepValidationRestException> SEQUENCE_INVALID =
        new ErrorCode<>("campaign_flow_step_sequence_invalid", 400,
            "Flow step sequence has to be between 1 and 65535.", "sequence");

    public static final ErrorCode<CampaignFlowStepValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_name_length_out_of_range", 400,
            "Name length has to be between 1 and 255", "name");

    public static final ErrorCode<CampaignFlowStepValidationRestException> ICON_COLOR_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_icon_color_length_out_of_range", 400,
            "Icon color length has to be between 1 and 64", "icon_color");

    public static final ErrorCode<CampaignFlowStepValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_description_length_out_of_range", 400,
            "Description length has to be between 1 and 255", "description");

    public static final ErrorCode<CampaignFlowStepValidationRestException> WORLD_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_word_length_out_of_range", 400,
            "Word length has to be between 1 and 255", "name", "word");

    public CampaignFlowStepValidationRestException(String uniqueId,
        ErrorCode<CampaignFlowStepValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
