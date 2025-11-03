package com.extole.client.rest.campaign.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public abstract class CampaignControllerTriggerRequest extends ComponentElementRequest {

    protected static final String PARENT_TRIGGER_GROUP_NAME = "parent_trigger_group_name";
    protected static final String TRIGGER_PHASE = "trigger_phase";
    protected static final String TRIGGER_NAME = "trigger_name";
    protected static final String TRIGGER_DESCRIPTION = "trigger_description";
    protected static final String ENABLED = "enabled";
    protected static final String NEGATED = "negated";

    private final Omissible<
        BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated;

    public CampaignControllerTriggerRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.parentTriggerGroupName = parentTriggerGroupName;
        this.description = description;
        this.enabled = enabled;
        this.negated = negated;
    }

    @JsonProperty(TRIGGER_PHASE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>>
        getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(TRIGGER_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(PARENT_TRIGGER_GROUP_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getParentTriggerGroupName() {
        return parentTriggerGroupName;
    }

    @JsonProperty(TRIGGER_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(NEGATED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getNegated() {
        return negated;
    }

    public abstract static class Builder<BUILDER extends Builder<BUILDER>>
        extends ComponentElementRequest.Builder<BUILDER> {

        protected Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase =
                Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated = Omissible.omitted();

        protected Builder() {
        }

        public BUILDER withTriggerPhase(
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> triggerPhase) {
            this.triggerPhase = Omissible.of(triggerPhase);
            return (BUILDER) this;
        }

        public BUILDER withTriggerPhase(CampaignControllerTriggerPhase triggerPhase) {
            this.triggerPhase = Omissible.of(Provided.of(triggerPhase));
            return (BUILDER) this;
        }

        public BUILDER withName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return (BUILDER) this;
        }

        public BUILDER withParentTriggerGroupName(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> parentTriggerGroupName) {
            this.parentTriggerGroupName = Omissible.of(parentTriggerGroupName);
            return (BUILDER) this;
        }

        public BUILDER withDescription(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return (BUILDER) this;
        }

        public BUILDER withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return (BUILDER) this;
        }

        public BUILDER withNegated(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated) {
            this.negated = Omissible.of(negated);
            return (BUILDER) this;
        }

    }

}
