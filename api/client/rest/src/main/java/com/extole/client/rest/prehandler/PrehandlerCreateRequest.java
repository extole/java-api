package com.extole.client.rest.prehandler;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.built.PrehandlerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.prehandler.action.request.PrehandlerActionRequest;
import com.extole.client.rest.prehandler.condition.request.PrehandlerConditionRequest;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class PrehandlerCreateRequest extends ComponentElementRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_ORDER = "order";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_CONDITIONS = "conditions";
    private static final String JSON_ACTIONS = "actions";

    private final BuildtimeEvaluatable<PrehandlerBuildtimeContext, String> name;
    private final Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Integer>> order;
    private final Omissible<Set<String>> tags;
    private final Omissible<List<? extends PrehandlerConditionRequest>> conditions;
    private final Omissible<List<? extends PrehandlerActionRequest>> actions;

    @JsonCreator
    public PrehandlerCreateRequest(
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<PrehandlerBuildtimeContext, String> name,
        @JsonProperty(JSON_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<PrehandlerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_ORDER) Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Integer>> order,
        @JsonProperty(JSON_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_CONDITIONS) Omissible<List<? extends PrehandlerConditionRequest>> conditions,
        @JsonProperty(JSON_ACTIONS) Omissible<List<? extends PrehandlerActionRequest>> actions,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.order = order;
        this.tags = tags;
        this.conditions = conditions;
        this.actions = actions;
    }

    @JsonProperty(JSON_NAME)
    @Schema(description = "Prehandler name must be unique.", required = true)
    public BuildtimeEvaluatable<PrehandlerBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    @Schema(nullable = true)
    public Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_ENABLED)
    @Schema(nullable = true, defaultValue = "false")
    public Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Boolean>> isEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_ORDER)
    @Schema(nullable = true, defaultValue = "0")
    public Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Integer>> getOrder() {
        return order;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_CONDITIONS)
    public Omissible<List<? extends PrehandlerConditionRequest>> getConditions() {
        return conditions;
    }

    @JsonProperty(JSON_ACTIONS)
    public Omissible<List<? extends PrehandlerActionRequest>> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ComponentElementRequest.Builder<Builder> {
        private BuildtimeEvaluatable<PrehandlerBuildtimeContext, String> name;
        private Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<PrehandlerBuildtimeContext, Integer>> order = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<List<? extends PrehandlerConditionRequest>> conditions = Omissible.omitted();
        private Omissible<List<? extends PrehandlerActionRequest>> actions = Omissible.omitted();

        public Builder withName(BuildtimeEvaluatable<PrehandlerBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(BuildtimeEvaluatable<PrehandlerBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<PrehandlerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withOrder(BuildtimeEvaluatable<PrehandlerBuildtimeContext, Integer> order) {
            this.order = Omissible.of(order);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withConditions(List<? extends PrehandlerConditionRequest> conditions) {
            this.conditions = Omissible.of(conditions);
            return this;
        }

        public Builder withActions(List<? extends PrehandlerActionRequest> actions) {
            this.actions = Omissible.of(actions);
            return this;
        }

        public PrehandlerCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new PrehandlerCreateRequest(name, description, enabled, order, tags, conditions, actions,
                componentIds,
                componentReferences);
        }
    }
}
