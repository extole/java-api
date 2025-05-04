package com.extole.consumer.rest.me.reward;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

public class ClaimRewardPollingResponse implements PollingResponse {
    private static final String REWARD_ID = "reward_id";
    private static final String ERROR = "error";

    private final String pollingId;
    private final PollingStatus status;
    private final String rewardId;
    private final ClaimRewardError error;

    public ClaimRewardPollingResponse(
        @JsonProperty(POLLING_ID) String pollingId,
        @JsonProperty(STATUS) PollingStatus status,
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(ERROR) ClaimRewardError error) {
        this.pollingId = pollingId;
        this.rewardId = rewardId;
        this.status = status;
        this.error = error;
    }

    @Override
    @JsonProperty(POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @Override
    @JsonProperty(STATUS)
    public PollingStatus getStatus() {
        return status;
    }

    @Nullable
    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @Nullable
    @JsonProperty(ERROR)
    public ClaimRewardError getError() {
        return error;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
