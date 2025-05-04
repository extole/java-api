package com.extole.client.rest.campaign.controller.action.incentivize.status.update;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionIncentivizeStatusUpdateValidationRestException
    extends CampaignControllerActionRestException {

    public static final ErrorCode<
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException> DATA_NAME_INVALID =
            new ErrorCode<>("data_name_invalid", 400, "Data name is invalid");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException> DATA_NAME_LENGTH_INVALID =
            new ErrorCode<>("data_name_length_invalid", 400,
                "Data name can't be blank or longer than maximum length", "name", "max_length");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException> DATA_VALUE_INVALID =
            new ErrorCode<>("data_value_invalid", 400, "Data value is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException> DATA_VALUE_LENGTH_INVALID =
            new ErrorCode<>("data_value_length_invalid", 400,
                "Data value can't be blank or longer than maximum length", "name", "max_length");

    public CampaignControllerActionIncentivizeStatusUpdateValidationRestException(
        String uniqueId,
        ErrorCode<CampaignControllerActionIncentivizeStatusUpdateValidationRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
