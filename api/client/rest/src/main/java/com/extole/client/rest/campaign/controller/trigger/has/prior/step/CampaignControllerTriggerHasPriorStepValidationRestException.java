package com.extole.client.rest.campaign.controller.trigger.has.prior.step;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerHasPriorStepValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_name_length_out_of_range", 400,
                "Filter name is not of valid length", "filter_name");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_NAME_CONTAINS_ILLEGAL_CHARACTER =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_name_contains_illegal_character", 400,
                "Filter name should contain alphanumeric, underscore, dash, dot and space characters only",
                "filter_name");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_PARTNER_EVENT_ID_NAME_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_partner_event_id_name_length", 400,
                "Filter partner event id name length must be between 1 and 2000", "filter_partner_event_id_name");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_PARTNER_EVENT_ID_NAME_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_partner_event_id_name_invalid", 400,
                "Filter partner event id name is invalid", "filter_partner_event_id_name");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_PARTNER_EVENT_ID_VALUE_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_partner_event_id_value_length", 400,
                "Filter partner event id value length must be between 1 and 2000", "filter_partner_event_id_value");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_PARTNER_EVENT_ID_VALUE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_partner_event_id_value_invalid", 400,
                "Filter partner event id value is invalid", "filter_partner_event_id_value");

    public static final ErrorCode<CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_MIN_AGE_INVALID =
        new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_min_age_invalid", 400,
            "Filter min age must be greater than zero", "filter_min_age");

    public static final ErrorCode<CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_MAX_AGE_INVALID =
        new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_max_age_invalid", 400,
            "Filter max age must be greater than zero", "filter_max_age");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_AGE_RANGE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_age_range_invalid", 400,
                "Filter min age must be less than filter max age", "filter_min_age", "filter_max_age");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_MIN_VALUE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_min_value_invalid", 400,
                "Filter min value must be greater than zero", "filter_min_value");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_MAX_VALUE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_max_value_invalid", 400,
                "Filter max value must be greater than zero", "filter_max_value");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_VALUE_RANGE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_value_range_invalid", 400,
                "Filter min value must be less than filter max value", "filter_min_value", "filter_max_value");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_EXPRESSION_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_expression_invalid", 400,
                "Filter expression is invalid", "filter_expression");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_EXPRESSION_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_expression_length", 400,
                "Filter expression length must be between 1 and 2000", "filter_expression");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_PROGRAM_LABEL_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_program_label_length", 400,
                "Filter program label length must be between 1 and 2000", "filter_program_label");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_PROGRAM_LABEL_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_program_label_invalid", 400,
                "Filter program label is invalid", "filter_program_label");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_CAMPAIGN_ID_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_campaign_id_length", 400,
                "Filter campaign id length must be between 1 and 2000", "filter_campaign_id");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_CAMPAIGN_ID_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_campaign_id_invalid", 400,
                "Filter campaign id is invalid", "filter_campaign_id");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_MIN_DATE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_min_date_invalid", 400,
                "Filter min date is invalid", "filter_min_date");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_MAX_DATE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_max_date_invalid", 400,
                "Filter max date is invalid", "filter_max_date");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> FILTER_DATE_RANGE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_filter_date_range_invalid", 400,
                "Filter min date must be before filter max date", "filter_min_date", "filter_max_date");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> SUM_OF_VALUE_MIN_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_sum_of_value_min_invalid", 400,
                "Sum of value min must be greater than zero", "sum_of_value_min");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> SUM_OF_VALUE_MAX_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_sum_of_value_max_invalid", 400,
                "Sum of value max must be greater than zero", "sum_of_value_max");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> SUM_OF_VALUE_RANGE_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_sum_of_value_range_invalid", 400,
                "Sum of value min must be less than sum of value max", "sum_of_value_min", "sum_of_value_max");

    public static final ErrorCode<CampaignControllerTriggerHasPriorStepValidationRestException> COUNT_MIN_INVALID =
        new ErrorCode<>("campaign_controller_trigger_has_prior_step_count_min_invalid", 400,
            "Count min must be non negative", "count_min");

    public static final ErrorCode<CampaignControllerTriggerHasPriorStepValidationRestException> COUNT_MAX_INVALID =
        new ErrorCode<>("campaign_controller_trigger_has_prior_step_count_max_invalid", 400,
            "Count max must be non negative", "count_max");

    public static final ErrorCode<CampaignControllerTriggerHasPriorStepValidationRestException> COUNT_RANGE_INVALID =
        new ErrorCode<>("campaign_controller_trigger_has_prior_step_count_range_invalid", 400,
            "Count min must be less than count max", "count_min", "count_max");

    public static final ErrorCode<CampaignControllerTriggerHasPriorStepValidationRestException> COUNT_MATCHES_INVALID =
        new ErrorCode<>("campaign_controller_trigger_has_prior_step_count_matches_invalid", 400,
            "Count matches must be greater or equal to zero", "count_matches");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> COUNT_MATCHES_NOT_IN_COUNT_RANGE =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_count_matches_not_in_count_range", 400,
                "At least one of count matches values must be in the count range", "count_matches", "count_min",
                "count_max");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorStepValidationRestException> COUNT_MATCHES_WITH_SUM_OF_VALUE_MIN_INVALID =
            new ErrorCode<>("campaign_controller_trigger_has_prior_step_count_matches_with_sum_of_value_min_invalid",
                400,
                "Count matches can't be just zero when sum of value min is configured", "count_matches",
                "sum_of_value_min");

    public CampaignControllerTriggerHasPriorStepValidationRestException(String uniqueId, ErrorCode<?> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
