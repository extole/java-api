package com.extole.client.rest.campaign.controller.action.expression;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionExpressionValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionExpressionValidationRestException> DATA_NAME_INVALID =
        new ErrorCode<>("data_name_invalid", 400, "Data name is invalid", "name");

    public static final ErrorCode<CampaignControllerActionExpressionValidationRestException> DATA_NAME_LENGTH_INVALID =
        new ErrorCode<>("data_name_length_invalid", 400,
            "The data name can't be blank or be longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionExpressionValidationRestException> DATA_VALUE_INVALID =
        new ErrorCode<>("data_value_invalid", 400, "Data value is invalid", "name");

    public static final ErrorCode<CampaignControllerActionExpressionValidationRestException> DATA_VALUE_LENGTH_INVALID =
        new ErrorCode<>("data_value_length_invalid", 400,
            "The data value can't be blank or be longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionExpressionValidationRestException> EXPRESSION_LENGTH_INVALID =
        new ErrorCode<>("expression_length_invalid", 400,
            "The expression can't be blank or be longer than maximum length", "max_length");

    public CampaignControllerActionExpressionValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionExpressionValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
