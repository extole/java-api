package com.extole.client.rest.campaign.label;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignLabelValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignLabelValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_label_name_out_of_range", 403,
            "Campaign label name is not of valid length", "name", "min_length", "max_length");

    public static final ErrorCode<CampaignLabelValidationRestException> NAME_ALREADY_IN_USE = new ErrorCode<>(
        "campaign_label_name_already_in_use", 403, "Campaign has already a label with this name", "name");

    public static final ErrorCode<CampaignLabelValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_label_name_contains_illegal_character", 403,
            "Campaign label name can only contain alphanumeric, dash and underscore characters", "name");

    public static final ErrorCode<CampaignLabelValidationRestException> NAME_MISSING =
        new ErrorCode<>("campaign_label_name_missing", 403, "Campaign label name is missing");

    public CampaignLabelValidationRestException(String uniqueId,
        ErrorCode<CampaignLabelValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
