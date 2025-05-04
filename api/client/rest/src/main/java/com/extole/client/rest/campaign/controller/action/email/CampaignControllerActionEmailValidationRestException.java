package com.extole.client.rest.campaign.controller.action.email;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionEmailValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionEmailValidationRestException> MISSING_ZONE_NAME =
        new ErrorCode<>("campaign_controller_action_email_missing_zone_name", 400, "Missing zone name");

    public static final ErrorCode<CampaignControllerActionEmailValidationRestException> ZONE_NAME_LENGTH_INVALID =
        new ErrorCode<>("campaign_controller_action_email_zone_name_out_of_range", 400,
            "Zone name length must be between 2 and 50 characters", "zone_name");

    public static final ErrorCode<
        CampaignControllerActionEmailValidationRestException> ZONE_NAME_CONTAINS_ILLEGAL_CHARACTER = new ErrorCode<>(
            "campaign_controller_action_email_zone_name_contains_illegal_character", 400,
            "Zone name can only contain alphanumeric and underscore characters", "zone_name");

    public static final ErrorCode<CampaignControllerActionEmailValidationRestException> DATA_NAME_INVALID =
        new ErrorCode<>("data_name_invalid", 400, "Data name is invalid");

    public static final ErrorCode<CampaignControllerActionEmailValidationRestException> DATA_NAME_LENGTH_INVALID =
        new ErrorCode<>("data_name_length_invalid", 400,
            "Data name can't be blank or longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionEmailValidationRestException> DATA_VALUE_INVALID =
        new ErrorCode<>("data_value_invalid", 400, "Data value is invalid", "name");

    public static final ErrorCode<CampaignControllerActionEmailValidationRestException> DATA_VALUE_LENGTH_INVALID =
        new ErrorCode<>("data_value_length_invalid", 400,
            "Data value can't be blank or longer than maximum length", "name", "max_length");

    public CampaignControllerActionEmailValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionEmailValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
