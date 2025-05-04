package com.extole.client.rest.campaign.controller.trigger.reward.event;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum RewardState {
    EARNED,
    FULFILLED,
    CANCELED,
    REVOKED,
    REDEEMED,
    FAILED
}
