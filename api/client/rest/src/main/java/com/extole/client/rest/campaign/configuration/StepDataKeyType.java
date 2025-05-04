package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum StepDataKeyType {

    NONE, PARTNER_PROFILE_KEY, PARTNER_EVENT_KEY, UNIQUE_PARTNER_EVENT_KEY, VALUE

}
