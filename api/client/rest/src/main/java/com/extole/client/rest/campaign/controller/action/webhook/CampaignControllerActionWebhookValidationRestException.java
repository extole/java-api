package com.extole.client.rest.campaign.controller.action.webhook;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionWebhookValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionWebhookValidationRestException> DATA_NAME_INVALID =
        new ErrorCode<>("data_name_invalid", 400, "Data name is invalid", "name");

    public static final ErrorCode<CampaignControllerActionWebhookValidationRestException> DATA_NAME_LENGTH_INVALID =
        new ErrorCode<>("data_name_length_invalid", 400,
            "The data name can't be blank or be longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionWebhookValidationRestException> DATA_VALUE_INVALID =
        new ErrorCode<>("data_value_invalid", 400, "Data value is invalid", "name");

    public static final ErrorCode<CampaignControllerActionWebhookValidationRestException> DATA_VALUE_LENGTH_INVALID =
        new ErrorCode<>("data_value_length_invalid", 400,
            "The data value can't be blank or be longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionWebhookValidationRestException> WEBHOOK_NOT_FOUND =
        new ErrorCode<>("webhook_not_found", 400, "Was unable to find webhook with id", "webhook_id");

    public static final ErrorCode<CampaignControllerActionWebhookValidationRestException> WEBHOOK_MISSING =
        new ErrorCode<>("webhook_missing", 400, "Webhook is required");

    public CampaignControllerActionWebhookValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionWebhookValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
