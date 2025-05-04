package com.extole.client.rest.campaign.configuration;

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
    HAS_PRIOR_REWARD,
    AUDIENCE_MEMBERSHIP_EVENT,
    AUDIENCE_MEMBERSHIP,
    HAS_IDENTITY,
    CLIENT_DOMAIN,
    LEGACY_LABEL_TARGETING

}
