package com.extole.consumer.rest.me.reward;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.signal.step.QualityResults;

public class PollingRewardResponse {

    public enum Status {
        PENDING, SUCCEEDED, FAILED
    }

    private static final String REWARD = "reward";
    private static final String STATUS = "status";
    private static final String QUALITY_RESULTS = "quality_results";

    private final Optional<RewardResponse> reward;
    private final Status status;
    private final List<QualityResults> qualityResults;

    public PollingRewardResponse(
        @JsonProperty(REWARD) Optional<RewardResponse> reward,
        @JsonProperty(STATUS) Status status,
        @JsonProperty(QUALITY_RESULTS) List<QualityResults> qualityResults) {
        this.reward = reward;
        this.status = status;
        this.qualityResults = qualityResults;
    }

    @JsonProperty(REWARD)
    public Optional<RewardResponse> getReward() {
        return reward;
    }

    @JsonProperty(STATUS)
    public Status getStatus() {
        return status;
    }

    @JsonProperty(QUALITY_RESULTS)
    public List<QualityResults> getQualityResults() {
        return qualityResults;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
