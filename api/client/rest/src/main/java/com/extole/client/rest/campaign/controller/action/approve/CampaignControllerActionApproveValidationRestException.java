package com.extole.client.rest.campaign.controller.action.approve;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionApproveValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> LEGACY_ACTION_ID_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_legacy_action_id_length", 400,
            "Legacy action id expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> EVENT_TYPE_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_event_type_length", 400,
            "Event type expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> PARTNER_EVENT_ID_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_partner_event_id_length", 400,
            "Partner event id expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> FORCE_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_force_length", 400,
            "Force expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> NOTE_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_note_length", 400,
            "Note expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> CAUSE_TYPE_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_cause_type_length", 400,
            "Cause type expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> POLLING_ID_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_polling_id_length", 400,
            "Polling id expression too long. Max length: 2000");

    public static final ErrorCode<CampaignControllerActionApproveValidationRestException> POLLING_NAME_LENGTH =
        new ErrorCode<>("campaign_controller_action_approve_polling_name_length", 400,
            "Polling name expression too long. Max length: 2000");

    public CampaignControllerActionApproveValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionApproveValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
