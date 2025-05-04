package com.extole.client.rest.campaign.controller.create.journey;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.step.journey.JourneyKeyContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class JourneyKeyCreateRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<
        CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value;

    public JourneyKeyCreateRequest(
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_VALUE) BuildtimeEvaluatable<
            CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>>
        getValue() {
        return value;
    }

    public static <T> Builder<T> builder(T caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<T> {

        private final T caller;

        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
        private BuildtimeEvaluatable<
            CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value;

        private Builder(T caller) {
            this.caller = caller;
        }

        public Builder<T> withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder<T> withValue(BuildtimeEvaluatable<
                CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value) {
            this.value = value;
            return this;
        }

        public T done() {
            return caller;
        }

        public JourneyKeyCreateRequest build() {
            return new JourneyKeyCreateRequest(name, value);
        }

    }

}
