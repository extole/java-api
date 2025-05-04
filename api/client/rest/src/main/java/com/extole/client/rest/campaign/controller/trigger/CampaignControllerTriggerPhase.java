package com.extole.client.rest.campaign.controller.trigger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignControllerTriggerPhase {
    MATCHING,
    QUALIFYING,
    QUALITY
}
