package com.extole.reporting.rest.posthandler;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.posthandler.action.ReportPostHandlerActionResponse;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionResponse;

public class ReportPostHandlerResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_ACTIONS = "actions";
    private static final String JSON_CONDITIONS = "conditions";

    private final String id;
    private final String name;
    private final boolean enabled;
    private final ZonedDateTime createdDate;
    private final List<ReportPostHandlerActionResponse> actions;
    private final List<ReportPostHandlerConditionResponse> conditions;

    @JsonCreator
    public ReportPostHandlerResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ENABLED) boolean enabled,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_ACTIONS) List<ReportPostHandlerActionResponse> actions,
        @JsonProperty(JSON_CONDITIONS) List<ReportPostHandlerConditionResponse> conditions) {
        this.id = id;
        this.enabled = enabled;
        this.name = name;
        this.createdDate = createdDate;
        this.actions = ImmutableList.copyOf(actions);
        this.conditions = ImmutableList.copyOf(conditions);
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_ENABLED)
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_ACTIONS)
    public List<ReportPostHandlerActionResponse> getActions() {
        return actions;
    }

    @JsonProperty(JSON_CONDITIONS)
    public List<ReportPostHandlerConditionResponse> getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
