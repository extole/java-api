package com.extole.client.rest.campaign.controller.action.fire.as.person;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionFireAsPersonIdentificationValidationRestException
    extends CampaignControllerActionRestException {

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonIdentificationValidationRestException>
            AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_action_fire_as_person_identification_value_length_out_of_range",
                400, "Person identification value is out of range. Maximum length is 2000 characters.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonIdentificationValidationRestException>
            AS_PERSON_IDENTIFICATION_VALUE_MISSING =
            new ErrorCode<>("campaign_controller_action_fire_as_person_identification_value_missing",
                400, "Person identification value is required.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonIdentificationValidationRestException> AS_PERSON_EVENT_KEY_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_action_fire_as_person_partner_event_key_length_out_of_range",
                400, "Partner event key is out of range. Maximum length is 2000 characters.");

    public static final ErrorCode<
        CampaignControllerActionFireAsPersonIdentificationValidationRestException> AS_PERSON_EVENT_KEY_MISSING =
            new ErrorCode<>("campaign_controller_action_fire_as_person_partner_event_key_missing",
                400, "Partner event key is required.");

    public CampaignControllerActionFireAsPersonIdentificationValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionFireAsPersonIdentificationValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
