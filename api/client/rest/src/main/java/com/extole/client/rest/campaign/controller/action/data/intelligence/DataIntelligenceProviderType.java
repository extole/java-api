package com.extole.client.rest.campaign.controller.action.data.intelligence;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum DataIntelligenceProviderType {
    NOOP,
    MAXMIND,
    EMAIL_AGE
}
