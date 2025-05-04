package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignControllerTriggerEventType {
    @Deprecated // TODO remove with ConsumerEventV2 in ENG-7272
    LEGACY_CONSUMER_EVENT,
    @Deprecated // TODO remove with ConsumerEventV2 in ENG-7272
    SCHEDULED,
    @Deprecated // TODO cleanup ENG-10082
    REFERRED_BY,

    INPUT,
    SHARE,
    STEP,
    IDENTIFIED,
    FORWARDED,
    REFERRED,
    DISPLACED,
    DISPLACED_BY,
    INTERNAL,
    MESSAGE,
    DATA_INTELLIGENCE,
    SHAREABLE,
    ASSET
}
