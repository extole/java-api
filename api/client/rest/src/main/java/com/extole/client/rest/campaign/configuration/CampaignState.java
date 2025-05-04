package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignState {
    NOT_LAUNCHED, LIVE, PAUSED, ENDED, STOPPED, ARCHIVED
}
