package com.extole.client.rest.campaign.controller.trigger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignControllerTriggerType {

    SHARE,
    EVENT,
    SCORE,
    ZONE_STATE,
    REFERRED_BY_EVENT,
    LEGACY_QUALITY,
    EXPRESSION,
    ACCESS,
    DATA_INTELLIGENCE_EVENT,
    HAS_PRIOR_STEP,
    MAXMIND,
    REWARD_EVENT,
    SEND_REWARD_EVENT,
    AUDIENCE_MEMBERSHIP,
    AUDIENCE_MEMBERSHIP_EVENT,
    HAS_PRIOR_REWARD,
    HAS_IDENTITY,
    CLIENT_DOMAIN,
    LEGACY_LABEL_TARGETING,
    STEP_EVENT,
    TARGETING,
    GROUP

}
