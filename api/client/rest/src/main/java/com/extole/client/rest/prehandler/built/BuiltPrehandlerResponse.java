package com.extole.client.rest.prehandler.built;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.prehandler.action.response.PrehandlerActionResponse;
import com.extole.client.rest.prehandler.condition.response.PrehandlerConditionResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltPrehandlerResponse extends ComponentElementResponse {
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_ORDER = "order";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_CONDITIONS = "conditions";
    private static final String JSON_ACTIONS = "actions";

    private final String id;
    private final String name;
    private final Optional<String> description;
    private final Boolean enabled;
    private final Integer order;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Set<String> tags;
    private final List<? extends PrehandlerConditionResponse> conditions;
    private final List<? extends PrehandlerActionResponse> actions;

    @JsonCreator
    public BuiltPrehandlerResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_ORDER) Integer order,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_TAGS) Set<String> tags,
        @JsonProperty(JSON_CONDITIONS) List<? extends PrehandlerConditionResponse> conditions,
        @JsonProperty(JSON_ACTIONS) List<? extends PrehandlerActionResponse> actions,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.order = order;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.tags =
            tags != null ? Collections.unmodifiableSet(new HashSet<>(tags)) : Collections.emptySet();
        this.conditions =
            conditions != null ? Collections.unmodifiableList(new ArrayList<>(conditions)) : Collections.emptyList();
        this.actions =
            actions != null ? Collections.unmodifiableList(new ArrayList<>(actions)) : Collections.emptyList();
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    @Schema(nullable = true)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_ENABLED)
    public Boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_ORDER)
    public Integer getOrder() {
        return order;
    }

    @JsonProperty(JSON_CREATED_DATE)
    @Schema(format = "date-time")
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    @Schema(format = "date-time")
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_CONDITIONS)
    public List<? extends PrehandlerConditionResponse> getConditions() {
        return conditions;
    }

    @JsonProperty(JSON_ACTIONS)
    public List<? extends PrehandlerActionResponse> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
