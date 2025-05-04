package com.extole.client.rest.campaign.controller.action;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignControllerActionType {
    APPROVE,
    CANCEL_REWARD,
    CREATE_MEMBERSHIP,
    CREATIVE,
    DATA_INTELLIGENCE,
    DECLINE,
    DISPLAY,
    EARN_REWARD,
    EMAIL,
    EXPRESSION,
    FIRE_AS_PERSON,
    FULFILL_REWARD,
    INCENTIVIZE,
    INCENTIVIZE_STATUS_UPDATE,
    REDEEM_REWARD,
    REMOVE_MEMBERSHIP,
    REVOKE_REWARD,
    SCHEDULE,
    SHARE_EVENT,
    SIGNAL,
    SIGNAL_V1,
    STEP_SIGNAL,
    WEBHOOK
}
