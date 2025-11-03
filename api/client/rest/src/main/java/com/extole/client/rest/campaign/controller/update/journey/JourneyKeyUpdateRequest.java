package com.extole.client.rest.campaign.controller.update.journey;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.step.journey.JourneyKeyContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class JourneyKeyUpdateRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name;
    private final Omissible<
        BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>>> value;

    public JourneyKeyUpdateRequest(
        @JsonProperty(JSON_NAME) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name,
        @JsonProperty(JSON_VALUE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>>> value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public
        Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>>>
        getValue() {
        return value;
    }

    public static <T> Builder<T> builder(T caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<T> {

        private final T caller;

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>>> value =
                Omissible.omitted();

        private Builder(T caller) {
            this.caller = caller;
        }

        public Builder<T> withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder<T> withValue(
            BuildtimeEvaluatable<CampaignBuildtimeContext,
                RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value) {
            this.value = Omissible.of(value);
            return this;
        }

        public T done() {
            return caller;
        }

        public JourneyKeyUpdateRequest build() {
            return new JourneyKeyUpdateRequest(name, value);
        }

    }

}
