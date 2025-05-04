package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignControllerTriggerShareChannel {
    ANY, EMAIL, FACEBOOK, TWITTER, EXTOLE_EMAIL, EXTOLE_TWITTER
}
