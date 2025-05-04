package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum CampaignControllerSelectorType {

    BEST_REFERRAL,
    BEST_REFERRAL_SAME_PROGRAM,
    TARGET,
    MATCHING_CAMPAIGN

}
