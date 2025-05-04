package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.person.Person;
import com.extole.api.trigger.audience.membership.AudienceMembershipTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerAudienceMembershipResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String HAVING_ANY_AUDIENCE_ID = "having_any_audience_id";
    private static final String PERSON_ID = "person_id";

    private final Set<Id<?>> havingAnyAudienceId;
    private final RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> personId;

    public BuiltCampaignControllerTriggerAudienceMembershipResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(HAVING_ANY_AUDIENCE_ID) Set<Id<?>> havingAnyAudienceId,
        @JsonProperty(PERSON_ID) RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> personId,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.havingAnyAudienceId = ImmutableSet.copyOf(havingAnyAudienceId);
        this.personId = personId;
    }

    @JsonProperty(HAVING_ANY_AUDIENCE_ID)
    public Set<Id<?>> getHavingAnyAudienceId() {
        return havingAnyAudienceId;
    }

    @JsonProperty(PERSON_ID)
    public RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> getPersonId() {
        return personId;
    }

}
