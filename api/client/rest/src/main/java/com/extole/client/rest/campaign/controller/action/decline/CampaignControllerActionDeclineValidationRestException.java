package com.extole.client.rest.campaign.controller.action.decline;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionDeclineValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> LEGACY_ACTION_ID_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_legacy_action_id_length", 400,
            "Legacy action id expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> EVENT_TYPE_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_event_type_length", 400,
            "Event type expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> PARTNER_EVENT_ID_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_partner_event_id_length", 400,
            "Partner event id expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> NOTE_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_note_length", 400,
            "Note expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> CAUSE_TYPE_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_cause_type_length", 400,
            "Cause type expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> POLLING_ID_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_polling_id_length", 400,
            "Polling id expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionDeclineValidationRestException> POLLING_NAME_LENGTH =
        new ErrorCode<>("campaign_controller_action_decline_polling_name_length", 400,
            "Polling name expression too long. Max length: 2000");

    public CampaignControllerActionDeclineValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionDeclineValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
