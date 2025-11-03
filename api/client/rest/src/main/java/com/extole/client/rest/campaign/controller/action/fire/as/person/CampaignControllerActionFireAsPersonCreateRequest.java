package com.extole.client.rest.campaign.controller.action.fire.as.person;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.person.Person;
import com.extole.api.step.action.fire.as.person.FireAsPersonActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionFireAsPersonCreateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_AS_PERSON_IDENTIFICATION = "as_person_identification";
    private static final String JSON_AS_PERSON_JOURNEY = "as_person_journey";
    private static final String JSON_DATA = "data";
    private static final String JSON_LABELS = "labels";
    private static final String JSON_PERSON_ID = "person_id";
    private static final String JSON_EXTRA_DATA = "extra_data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName;
    private final Omissible<FireAsPersonIdentification> asPersonIdentification;
    private final Omissible<Optional<FireAsPersonJourney>> asPersonJourney;
    private final Omissible<Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>, BuildtimeEvaluatable<
        ControllerBuildtimeContext, RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>>> data;
    private final Omissible<Set<String>> labels;
    private final Omissible<
        BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>>> personId;
    private final Omissible<
        BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>>> extraData;

    @JsonCreator
    private CampaignControllerActionFireAsPersonCreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_EVENT_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName,
        @JsonProperty(JSON_AS_PERSON_IDENTIFICATION) Omissible<FireAsPersonIdentification> asPersonIdentification,
        @JsonProperty(JSON_AS_PERSON_JOURNEY) Omissible<Optional<FireAsPersonJourney>> asPersonJourney,
        @JsonProperty(JSON_DATA) Omissible<Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>>> data,
        @JsonProperty(JSON_LABELS) Omissible<Set<String>> labels,
        @JsonProperty(JSON_PERSON_ID) Omissible<
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>>> personId,
        @JsonProperty(JSON_EXTRA_DATA) Omissible<
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>>> extraData) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.eventName = eventName;
        this.asPersonIdentification = asPersonIdentification;
        this.asPersonJourney = asPersonJourney;
        this.data = data;
        this.labels = labels;
        this.personId = personId;
        this.extraData = extraData;
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

    @Deprecated // TODO Migrate to personId - ENG-26910
    @JsonProperty(JSON_AS_PERSON_IDENTIFICATION)
    public Omissible<FireAsPersonIdentification> getAsPersonIdentification() {
        return asPersonIdentification;
    }

    @JsonProperty(JSON_AS_PERSON_JOURNEY)
    public Omissible<Optional<FireAsPersonJourney>> getAsPersonJourney() {
        return asPersonJourney;
    }

    @JsonProperty(JSON_DATA)
    public
        Omissible<Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>>>
        getData() {
        return data;
    }

    @JsonProperty(JSON_LABELS)
    public Omissible<Set<String>> getLabels() {
        return labels;
    }

    // TODO Make person ID response non-optional - ENG-26910
    @JsonProperty(JSON_PERSON_ID)
    public Omissible<
        BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>>>
        getPersonId() {
        return personId;
    }

    @JsonProperty(JSON_EXTRA_DATA)
    public Omissible<
        BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>>>
        getExtraData() {
        return extraData;
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
        private Omissible<Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>>> data =
                    Omissible.omitted();
        private Omissible<Set<String>> labels = Omissible.omitted();
        private Omissible<
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>>> personId = Omissible.omitted();
        private Omissible<
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>>> extraData =
                    Omissible.omitted();

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

        public Builder withAsPersonJourney(FireAsPersonJourney asPersonJourney) {
            this.asPersonJourney = Omissible.of(Optional.of(asPersonJourney));
            return this;
        }

        public Builder withData(
            Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>, BuildtimeEvaluatable<
                ControllerBuildtimeContext, RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withLabels(Set<String> labels) {
            this.labels = Omissible.of(labels);
            return this;
        }

        public Builder withPersonId(
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>> personId) {
            this.personId = Omissible.of(personId);
            return this;
        }

        public Builder withExtraData(
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>> extraData) {
            this.extraData = Omissible.of(extraData);
            return this;
        }

        @Override
        public CampaignControllerActionFireAsPersonCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionFireAsPersonCreateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                eventName,
                asPersonIdentification,
                asPersonJourney,
                data,
                labels,
                personId,
                extraData);
        }

    }

}
