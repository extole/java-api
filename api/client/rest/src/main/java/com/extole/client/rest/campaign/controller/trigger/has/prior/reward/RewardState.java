package com.extole.client.rest.campaign.controller.trigger.has.prior.reward;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum RewardState {
    EARNED, FULFILLED, SENT, REDEEMED, FAILED, CANCELED, REVOKED
}
