package com.extole.client.rest.rewards;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum RewardStateType {

    EARNED, FULFILLED, SENT, REDEEMED, FAILED, CANCELED, REVOKED

}
