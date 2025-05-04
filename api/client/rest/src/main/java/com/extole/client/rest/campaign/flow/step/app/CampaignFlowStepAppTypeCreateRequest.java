package com.extole.client.rest.campaign.flow.step.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;

public final class CampaignFlowStepAppTypeCreateRequest {

    private static final String JSON_NAME = "name";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;

    @JsonCreator
    public CampaignFlowStepAppTypeCreateRequest(
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
        this.name = name;
    }

    public static <T> Builder<T> builder(T caller) {
        return new Builder<>(caller);
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder<T> {

        private final T caller;
        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;

        private Builder(T caller) {
            this.caller = caller;
        }

        public Builder<T> withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public T done() {
            return caller;
        }

        public CampaignFlowStepAppTypeCreateRequest build() {
            return new CampaignFlowStepAppTypeCreateRequest(name);
        }

    }

}
