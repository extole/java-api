package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignValidationRestException> NAME_LENGTH_OUT_OF_RANGE = new ErrorCode<>(
        "campaign_name_out_of_range", 403, "Campaign name length is out of range", "name", "min_length", "max_length");

    public static final ErrorCode<CampaignValidationRestException> NAME_IS_MISSING = new ErrorCode<>(
        "campaign_name_is_missing", 403, "Campaign name is missing");

    public static final ErrorCode<CampaignValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_description_out_of_range", 403,
            "Campaign description length must be between 2 and 250 characters", "description");

    public static final ErrorCode<CampaignValidationRestException> NAME_ALREADY_USED =
        new ErrorCode<>("campaign_name_already_used", 403, "Campaign name is already used by another campaign", "name");

    public static final ErrorCode<CampaignValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_name_illegal_character", 403,
            "Campaign name can only contain alphanumeric, space, dash, parenthesis, slash, " +
                "colon, comma, period, underscore, dollar and percentage",
            "name");

    public static final ErrorCode<CampaignValidationRestException> INVALID_PROGRAM_TYPE =
        new ErrorCode<>("invalid_program_type", 400,
            "Invalid program type, length must be between 1 and 128", "program_type");

    public static final ErrorCode<CampaignValidationRestException> PROGRAM_TYPE_EMPTY =
        new ErrorCode<>("program_type_empty", 400,
            "Program type cannot be empty or null");

    public static final ErrorCode<CampaignValidationRestException> INVALID_THEME_NAME =
        new ErrorCode<>("invalid_theme_name", 400,
            "Invalid theme_name, length must be between 1 and 255", "theme_name");

    public static final ErrorCode<CampaignValidationRestException> CAMPAIGN_WITH_PENDING_CHANGES = new ErrorCode<>(
        "campaign_has_pending_changes", 400, "Campaign can't be pushed live since it has pending changes that are not "
            + "discarded or published",
        "campaign_id");

    public static final ErrorCode<CampaignValidationRestException> INVALID_TAG =
        new ErrorCode<>("campaign_invalid_tag", 400, "A tag contains invalid characters", "tag");

    public static final ErrorCode<CampaignValidationRestException> TAGS_TOO_LONG =
        new ErrorCode<>("campaign_tags_too_long", 400, "Tags length is too long", "tags_length", "max_tags_length");

    public static final ErrorCode<CampaignValidationRestException> MISSING_STEP_DATA_NAME =
        new ErrorCode<>("missing_step_data_name", 400, "Missing step data name");

    public static final ErrorCode<CampaignValidationRestException> STEP_DATA_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("step_data_name_length_out_of_range", 400,
            "Step data name length is out of range. Max 255 characters", "name");

    public static final ErrorCode<CampaignValidationRestException> MISSING_STEP_DATA_VALUE =
        new ErrorCode<>("missing_step_data_value", 400, "Missing step data value", "name");

    public static final ErrorCode<CampaignValidationRestException> STEP_DATA_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("step_data_value_expression_length_out_of_range", 400,
            "Step data value expression length is out of range. Max 2000 characters", "expression", "name");

    public static final ErrorCode<
        CampaignValidationRestException> STEP_DATA_DEFAULT_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("step_data_default_value_expression_length_out_of_range", 400,
                "Step data default value expression length is out of range. Max 2000 characters", "expression", "name");

    public static final ErrorCode<CampaignValidationRestException> DUPLICATE_STEP_DATA_NAME =
        new ErrorCode<>("duplicate_step_data_name", 400, "Step data name is duplicated", "name");

    public CampaignValidationRestException(String uniqueId, ErrorCode<CampaignValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
