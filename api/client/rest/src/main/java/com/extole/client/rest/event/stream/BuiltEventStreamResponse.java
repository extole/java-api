package com.extole.client.rest.event.stream;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class BuiltEventStreamResponse {

    private static final String EVENT_STREAM_ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String FILTERS = "filters";
    private static final String TAGS = "tags";
    private static final String COMPONENTS_IDS = "component_ids";
    private static final String STOP_AT = "stop_at";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final Id<?> id;
    private final String name;
    private final Optional<String> description;
    private final List<EventStreamFilterResponse> filters;
    private final Set<String> tags;
    private final List<Id<?>> componentReferences;
    private final Instant stopAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    public BuiltEventStreamResponse(@JsonProperty(EVENT_STREAM_ID) Id<?> id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(FILTERS) List<EventStreamFilterResponse> filters,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(COMPONENTS_IDS) List<Id<?>> componentIds,
        @JsonProperty(STOP_AT) Instant stopAt,
        @JsonProperty(CREATED_DATE) Instant createdAt,
        @JsonProperty(UPDATED_DATE) Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.filters = filters;
        this.tags = tags;
        this.componentReferences = componentIds;
        this.stopAt = stopAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(EVENT_STREAM_ID)
    public Id<?> getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(FILTERS)
    public List<EventStreamFilterResponse> getFilters() {
        return filters;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(COMPONENTS_IDS)
    public List<Id<?>> getComponentReferences() {
        return componentReferences;
    }

    @JsonProperty(STOP_AT)
    public Instant getStopAt() {
        return stopAt;
    }

    @JsonProperty(CREATED_DATE)
    public Instant getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(UPDATED_DATE)
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
