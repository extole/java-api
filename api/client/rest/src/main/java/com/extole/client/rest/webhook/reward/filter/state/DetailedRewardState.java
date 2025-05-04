package com.extole.client.rest.webhook.reward.filter.state;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum DetailedRewardState {

    EARNED, FULFILLED, FULFILL_FAILED, SENT, REDEEMED, FAILED, CANCELED, REVOKED

}
