package com.extole.client.rest.campaign.built.controller.action.fire.as.person;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonJourney;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.id.Id;

public class BuiltCampaignControllerActionFireAsPersonResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_AS_PERSON_IDENTIFICATION = "as_person_identification";
    private static final String JSON_AS_PERSON_JOURNEY = "as_person_journey";
    private static final String JSON_DATA = "data";
    private static final String JSON_LABELS = "labels";

    private final String eventName;
    private final FireAsPersonIdentification asPersonIdentification;
    private final Optional<FireAsPersonJourney> asPersonJourney;
    private final Map<String, String> data;
    private final Set<String> labels;

    @JsonCreator
    public BuiltCampaignControllerActionFireAsPersonResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_EVENT_NAME) String eventName,
        @JsonProperty(JSON_AS_PERSON_IDENTIFICATION) FireAsPersonIdentification asPersonIdentification,
        @JsonProperty(JSON_AS_PERSON_JOURNEY) Optional<FireAsPersonJourney> asPersonJourney,
        @JsonProperty(JSON_DATA) Map<String, String> data,
        @JsonProperty(JSON_LABELS) Set<String> labels,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.FIRE_AS_PERSON, quality, enabled, componentIds,
            componentReferences);
        this.eventName = eventName;
        this.asPersonIdentification = asPersonIdentification;
        this.asPersonJourney = asPersonJourney;
        this.data = data != null ? ImmutableMap.copyOf(data) : null;
        this.labels = labels != null ? ImmutableSet.copyOf(labels) : null;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_AS_PERSON_IDENTIFICATION)
    public FireAsPersonIdentification getAsPersonIdentification() {
        return asPersonIdentification;
    }

    @JsonProperty(JSON_AS_PERSON_JOURNEY)
    public Optional<FireAsPersonJourney> getAsPersonJourney() {
        return asPersonJourney;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(JSON_LABELS)
    public Set<String> getLabels() {
        return labels;
    }

}
