package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum StepFilterScope {
    CLIENT, ATTRIBUTED, PROGRAM, CAMPAIGN, JOURNEY
}
