package com.extole.client.rest.campaign.controller.update;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.update.journey.JourneyKeyUpdateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignJourneyEntryUpdateRequest extends CampaignStepUpdateRequest {

    public static final String STEP_TYPE_JOURNEY_ENTRY = "JOURNEY_ENTRY";

    private static final String JSON_JOURNEY_NAME = "journey_name";
    private static final String JSON_PRIORITY = "priority";
    private static final String JSON_KEY = "key";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> journeyName;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal>> priority;
    private final Omissible<Optional<JourneyKeyUpdateRequest>> key;

    public CampaignJourneyEntryUpdateRequest(
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_JOURNEY_NAME) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> journeyName,
        @JsonProperty(JSON_PRIORITY) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal>> priority,
        @JsonProperty(JSON_KEY) Omissible<Optional<JourneyKeyUpdateRequest>> key) {
        super(enabled, componentIds, componentReferences);
        this.journeyName = journeyName;
        this.priority = priority;
        this.key = key;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.JOURNEY_ENTRY;
    }

    @JsonProperty(JSON_JOURNEY_NAME)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JSON_PRIORITY)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal>> getPriority() {
        return priority;
    }

    @JsonProperty(JSON_KEY)
    public Omissible<Optional<JourneyKeyUpdateRequest>> getKey() {
        return key;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends CampaignStepUpdateRequest.Builder<Builder, CampaignJourneyEntryUpdateRequest> {

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> journeyName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal>> priority = Omissible.omitted();
        private Omissible<Optional<JourneyKeyUpdateRequest.Builder<Builder>>> keyBuilder = Omissible.omitted();

        private Builder() {
        }

        public Builder withJourneyName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> journeyName) {
            this.journeyName = Omissible.of(journeyName);
            return this;
        }

        public Builder withJourneyName(String journeyName) {
            this.journeyName = Omissible.of(Provided.of(journeyName));
            return this;
        }

        public Builder withPriority(BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> priority) {
            this.priority = Omissible.of(priority);
            return this;
        }

        public Builder withPriority(DeweyDecimal priority) {
            this.priority = Omissible.of(Provided.of(priority));
            return this;
        }

        public JourneyKeyUpdateRequest.Builder<Builder> withKey() {
            this.keyBuilder = Omissible.of(Optional.of(JourneyKeyUpdateRequest.builder(this)));
            return keyBuilder.getValue().get();
        }

        public Builder removeKey() {
            this.keyBuilder = Omissible.of(Optional.empty());
            return this;
        }

        @Override
        public CampaignJourneyEntryUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignJourneyEntryUpdateRequest(
                enabled,
                componentIds,
                componentReferences,
                journeyName,
                priority,
                keyBuilder
                    .<Omissible<Optional<JourneyKeyUpdateRequest>>>map(
                        value -> value.isPresent() ? Omissible.of(Optional.of(value.get().build()))
                            : Omissible.of(Optional.empty()))
                    .orElse(Omissible.omitted()));
        }

    }

}
