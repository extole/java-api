package com.extole.client.rest.campaign.controller.action.fire.as.person;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionFireAsPersonUpdateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_AS_PERSON_IDENTIFICATION = "as_person_identification";
    private static final String JSON_AS_PERSON_JOURNEY = "as_person_journey";
    private static final String JSON_DATA = "data";
    private static final String JSON_LABELS = "labels";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName;
    private final Omissible<FireAsPersonIdentification> asPersonIdentification;
    private final Omissible<Optional<FireAsPersonJourney>> asPersonJourney;
    private final Omissible<Map<String, String>> data;
    private final Omissible<Set<String>> labels;

    @JsonCreator
    private CampaignControllerActionFireAsPersonUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_EVENT_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName,
        @JsonProperty(JSON_AS_PERSON_IDENTIFICATION) Omissible<FireAsPersonIdentification> asPersonIdentification,
        @JsonProperty(JSON_AS_PERSON_JOURNEY) Omissible<Optional<FireAsPersonJourney>> asPersonJourney,
        @JsonProperty(JSON_DATA) Omissible<Map<String, String>> data,
        @JsonProperty(JSON_LABELS) Omissible<Set<String>> labels) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.eventName = eventName;
        this.asPersonIdentification = asPersonIdentification;
        this.asPersonJourney = asPersonJourney;
        this.data = data;
        this.labels = labels;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_AS_PERSON_IDENTIFICATION)
    public Omissible<FireAsPersonIdentification> getAsPersonIdentification() {
        return asPersonIdentification;
    }

    @JsonProperty(JSON_AS_PERSON_JOURNEY)
    public Omissible<Optional<FireAsPersonJourney>> getAsPersonJourney() {
        return asPersonJourney;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<Map<String, String>> getData() {
        return data;
    }

    @JsonProperty(JSON_LABELS)
    public Omissible<Set<String>> getLabels() {
        return labels;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName = Omissible.omitted();
        private Omissible<FireAsPersonIdentification> asPersonIdentification = Omissible.omitted();
        private Omissible<Optional<FireAsPersonJourney>> asPersonJourney = Omissible.omitted();
        private Omissible<Map<String, String>> data = Omissible.omitted();
        private Omissible<Set<String>> labels = Omissible.omitted();

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withEventName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName) {
            this.eventName = Omissible.of(eventName);
            return this;
        }

        public Builder withAsPersonIdentification(FireAsPersonIdentification asPersonIdentification) {
            this.asPersonIdentification = Omissible.of(asPersonIdentification);
            return this;
        }

        public Builder withAsPersonJourney(Optional<FireAsPersonJourney> asPersonJourney) {
            this.asPersonJourney = Omissible.of(asPersonJourney);
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withLabels(Set<String> labels) {
            this.labels = Omissible.of(labels);
            return this;
        }

        public CampaignControllerActionFireAsPersonUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionFireAsPersonUpdateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                eventName,
                asPersonIdentification,
                asPersonJourney,
                data,
                labels);
        }

    }

}
