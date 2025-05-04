package com.extole.client.rest.campaign.controller.action.signal.v1;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionSignalV1ValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<
        CampaignControllerActionSignalV1ValidationRestException> SIGNAL_POLLING_ID_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_action_signal_signal_polling_id_out_of_range", 403,
                "Signal polling id value length is out of range. Max 2000 chars", "signal_polling_id");

    public static final ErrorCode<CampaignControllerActionSignalV1ValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("campaign_controller_action_signal_data_attribute_name_invalid", 403,
            "Data attribute name is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionSignalV1ValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_signal_data_attribute_name_length_out_of_range", 403,
                "Data attribute name length is out of range. Max 200 chars", "name");

    public static final ErrorCode<
        CampaignControllerActionSignalV1ValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
            new ErrorCode<>("campaign_controller_action_signal_data_attribute_value_invalid", 403,
                "Data attribute value is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionSignalV1ValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_signal_data_attribute_value_length_out_of_range", 403,
                "Data attribute value length is out of range. Max 2000 chars", "name");

    public CampaignControllerActionSignalV1ValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionSignalV1ValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
