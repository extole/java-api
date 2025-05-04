package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.person.Person;
import com.extole.api.trigger.audience.membership.AudienceMembershipTriggerContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerAudienceMembershipConfiguration
    extends CampaignControllerTriggerConfiguration {

    private static final String HAVING_ANY_AUDIENCE_ID = "having_any_audience_id";
    private static final String PERSON_ID = "person_id";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> havingAnyAudienceId;
    private final RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> personId;

    public CampaignControllerTriggerAudienceMembershipConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(HAVING_ANY_AUDIENCE_ID) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<Id<?>>> havingAnyAudienceId,
        @JsonProperty(PERSON_ID) RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> personId,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP, triggerPhase, name, description,
            enabled, negated, componentReferences);
        this.havingAnyAudienceId = havingAnyAudienceId;
        this.personId = personId;
    }

    @JsonProperty(HAVING_ANY_AUDIENCE_ID)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> getHavingAnyAudienceId() {
        return havingAnyAudienceId;
    }

    @JsonProperty(PERSON_ID)
    public RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> getPersonId() {
        return personId;
    }

}
