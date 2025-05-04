package com.extole.client.rest.campaign.flow.step.metric;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignFlowStepMetricValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_NAME_MISSING =
        new ErrorCode<>("campaign_flow_step_metric_name_missing", 400, "Metric name is missing");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_UNIT_MISSING =
        new ErrorCode<>("campaign_flow_step_metric_unit_missing", 400, "Metric unit is missing");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_TAGS_MISSING =
        new ErrorCode<>("campaign_flow_step_metric_tags_missing", 400, "Metric tags are missing");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_EXPRESSION_MISSING =
        new ErrorCode<>("campaign_flow_step_metric_expression_missing", 400, "Metric expression is missing");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_metric_name_length_out_of_range", 400,
            "Metric name length has to be between 1 and 255", "metric_name");

    public static final ErrorCode<
        CampaignFlowStepMetricValidationRestException> METRIC_DESCRIPTION_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_flow_step_metric_description_length_out_of_range", 400,
                "Metric description length has to be between 1 and 2000", "metric_description");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_EXPRESSION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_metric_expression_length_out_of_range", 400,
            "Metric expression length has to be between 1 and 2000", "metric_expression");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_EXPRESSION_INVALID =
        new ErrorCode<>("campaign_flow_step_metric_expression_invalid", 400,
            "Metric expression is invalid", "metric_expression");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_UNIT_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_flow_step_metric_unit_length_out_of_range", 400,
            "Metric unit length has to be between 1 and 64", "unit");

    public static final ErrorCode<CampaignFlowStepMetricValidationRestException> METRIC_DUPLICATE_EXISTS =
        new ErrorCode<>("campaign_flow_step_metric_duplicate_exists", 400,
            "Another metric with the same name exists in the flow step", "metric_name", "flow_step_name");

    public CampaignFlowStepMetricValidationRestException(String uniqueId,
        ErrorCode<CampaignFlowStepMetricValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
