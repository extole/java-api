package com.extole.client.rest.v0;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ReviewStatus {
    NO_REWARD_RULES, PENDING, DECLINED, APPROVED, CAMPAIGN_STOPPED, FULFILLMENT_ERROR, CUSTOM, NOT_APPLICABLE,
    EVALUATING
}
