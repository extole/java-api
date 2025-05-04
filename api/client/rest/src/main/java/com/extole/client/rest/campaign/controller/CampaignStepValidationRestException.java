package com.extole.client.rest.campaign.controller;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignStepValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignStepValidationRestException> JOURNEY_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("journey_name_length_out_of_range", 400,
            "Journey name is not of valid length", "journey_name", "min_length", "max_length");

    public static final ErrorCode<CampaignStepValidationRestException> JOURNEY_NAME_INVALID =
        new ErrorCode<>("journey_name_invalid", 400, "Journey name contains invalid characters", "journey_name");

    public static final ErrorCode<CampaignStepValidationRestException> NULL_JOURNEY_NAME =
        new ErrorCode<>("null_journey_name", 400, "Journey name cannot be null");

    public CampaignStepValidationRestException(
        String uniqueId,
        ErrorCode<CampaignStepValidationRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
