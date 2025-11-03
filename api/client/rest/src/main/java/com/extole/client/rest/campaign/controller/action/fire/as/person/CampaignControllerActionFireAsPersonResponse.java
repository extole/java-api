package com.extole.client.rest.campaign.controller.action.fire.as.person;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.person.Person;
import com.extole.api.step.action.fire.as.person.FireAsPersonActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionFireAsPersonResponse extends CampaignControllerActionResponse {

    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_AS_PERSON_IDENTIFICATION = "as_person_identification";
    private static final String JSON_AS_PERSON_JOURNEY = "as_person_journey";
    private static final String JSON_DATA = "data";
    private static final String JSON_LABELS = "labels";
    private static final String JSON_PERSON_ID = "person_id";
    private static final String JSON_EXTRA_DATA = "extra_data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName;
    private final Optional<FireAsPersonIdentification> asPersonIdentification;
    private final Optional<FireAsPersonJourney> asPersonJourney;
    private final Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>, BuildtimeEvaluatable<
        ControllerBuildtimeContext, RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>> data;
    private final Set<String> labels;
    private final BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>> personId;
    private final BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>> extraData;

    @JsonCreator
    public CampaignControllerActionFireAsPersonResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_EVENT_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName,
        @JsonProperty(JSON_AS_PERSON_IDENTIFICATION) Optional<FireAsPersonIdentification> asPersonIdentification,
        @JsonProperty(JSON_AS_PERSON_JOURNEY) Optional<FireAsPersonJourney> asPersonJourney,
        @JsonProperty(JSON_DATA) Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>> data,
        @JsonProperty(JSON_LABELS) Set<String> labels,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_PERSON_ID) BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>> personId,
        @JsonProperty(JSON_EXTRA_DATA) BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>> extraData) {
        super(actionId, CampaignControllerActionType.FIRE_AS_PERSON, quality, enabled, componentIds,
            componentReferences);
        this.eventName = eventName;
        this.asPersonIdentification = asPersonIdentification;
        this.asPersonJourney = asPersonJourney;
        this.data = data != null ? ImmutableMap.copyOf(data) : null;
        this.labels = labels != null ? ImmutableSet.copyOf(labels) : null;
        this.personId = personId;
        this.extraData = extraData;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getEventName() {
        return eventName;
    }

    @Deprecated // TODO Migrate to personId - ENG-26910
    @JsonProperty(JSON_AS_PERSON_IDENTIFICATION)
    public Optional<FireAsPersonIdentification> getAsPersonIdentification() {
        return asPersonIdentification;
    }

    @JsonProperty(JSON_AS_PERSON_JOURNEY)
    public Optional<FireAsPersonJourney> getAsPersonJourney() {
        return asPersonJourney;
    }

    @JsonProperty(JSON_DATA)
    public
        Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<FireAsPersonActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

    @JsonProperty(JSON_LABELS)
    public Set<String> getLabels() {
        return labels;
    }

    // TODO Make person ID response non-optional - ENG-26910
    @JsonProperty(JSON_PERSON_ID)
    public BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<FireAsPersonActionContext, Optional<Id<Person>>>>
        getPersonId() {
        return personId;
    }

    @JsonProperty(JSON_EXTRA_DATA)
    public BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<FireAsPersonActionContext, Map<String, Optional<Object>>>>
        getExtraData() {
        return extraData;
    }

}
