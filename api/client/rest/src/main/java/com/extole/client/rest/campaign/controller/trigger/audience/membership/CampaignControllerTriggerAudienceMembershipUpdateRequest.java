package com.extole.client.rest.campaign.controller.trigger.audience.membership;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.person.Person;
import com.extole.api.trigger.audience.membership.AudienceMembershipTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerTriggerAudienceMembershipUpdateRequest extends CampaignControllerTriggerRequest {

    private static final String HAVING_ANY_AUDIENCE_ID = "having_any_audience_id";
    private static final String PERSON_ID = "person_id";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> havingAnyAudienceId;
    private final Omissible<RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>>> personId;

    @JsonCreator
    public CampaignControllerTriggerAudienceMembershipUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(HAVING_ANY_AUDIENCE_ID) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> havingAnyAudienceId,
        @JsonProperty(PERSON_ID) Omissible<
            RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>>> personId,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.havingAnyAudienceId = havingAnyAudienceId;
        this.personId = personId;
    }

    @JsonProperty(HAVING_ANY_AUDIENCE_ID)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> getHavingAnyAudienceId() {
        return havingAnyAudienceId;
    }

    @JsonProperty(PERSON_ID)
    public Omissible<RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>>> getPersonId() {
        return personId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> havingAnyAudienceId =
            Omissible.omitted();
        private Omissible<RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>>> personId =
            Omissible.omitted();

        private Builder() {
        }

        public Builder
            withHavingAnyAudienceId(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> havingAnyAudienceId) {
            this.havingAnyAudienceId = Omissible.of(havingAnyAudienceId);
            return this;
        }

        public Builder
            withPersonId(RuntimeEvaluatable<AudienceMembershipTriggerContext, Optional<Id<Person>>> personId) {
            this.personId = Omissible.of(personId);
            return this;
        }

        public CampaignControllerTriggerAudienceMembershipUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerAudienceMembershipUpdateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                havingAnyAudienceId,
                personId,
                componentIds,
                componentReferences);
        }
    }

}
