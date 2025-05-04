package com.extole.client.rest.campaign.controller.action.signal;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionSignalValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionSignalValidationRestException> DATA_NAME_INVALID =
        new ErrorCode<>("data_name_invalid", 400, "Data name is invalid");

    public static final ErrorCode<CampaignControllerActionSignalValidationRestException> DATA_NAME_LENGTH_INVALID =
        new ErrorCode<>("data_name_length_invalid", 400,
            "Data name can't be blank or longer than maximum length", "name", "max_length");

    public static final ErrorCode<CampaignControllerActionSignalValidationRestException> DATA_VALUE_INVALID =
        new ErrorCode<>("data_value_invalid", 400, "Data value is invalid", "name");

    public static final ErrorCode<CampaignControllerActionSignalValidationRestException> DATA_VALUE_LENGTH_INVALID =
        new ErrorCode<>("data_value_length_invalid", 400,
            "Data value can't be blank or longer than maximum length", "name", "max_length");

    public static final ErrorCode<
        CampaignControllerActionSignalValidationRestException> SIGNAL_POLLING_ID_LENGTH_INVALID =
            new ErrorCode<>("signal_polling_id_length_invalid", 400,
                "Signal polling id can't be blank or longer than maximum length", "max_length");

    public static final ErrorCode<CampaignControllerActionSignalValidationRestException> NAME_LENGTH_INVALID =
        new ErrorCode<>("name_length_invalid", 400, "Name can't be longer than maximum length", "max_length");

    public CampaignControllerActionSignalValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionSignalValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
