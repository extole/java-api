package com.extole.reporting.rest.batch;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.batch.column.request.BatchJobColumnRequest;
import com.extole.reporting.rest.batch.data.source.request.BatchJobDataSourceRequest;

public final class BatchJobCreateRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_DEFAULT_EVENT_NAME = "default_event_name";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_EVENT_DATA = "event_data";
    private static final String JSON_EVENT_COLUMNS = "event_columns";
    private static final String JSON_COLUMNS = "columns";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_DATA_SOURCE = "data_source";

    private final Omissible<String> name;
    private final Omissible<String> eventName;
    private final Omissible<String> defaultEventName;
    private final Omissible<Set<String>> tags;
    private final Omissible<Set<BatchJobScope>> scopes;
    private final Omissible<Set<String>> eventColumns;
    private final Omissible<Set<BatchJobColumnRequest>> columns;
    private final Omissible<Map<String, String>> eventData;
    private final BatchJobDataSourceRequest dataSource;

    BatchJobCreateRequest(
        @Parameter(description = "BatchJob name") @JsonProperty(JSON_NAME) Omissible<String> name,
        @Parameter(description = "Optional event name") @JsonProperty(JSON_EVENT_NAME) Omissible<String> eventName,
        @Parameter(description = "Optional default event name")
        @JsonProperty(JSON_DEFAULT_EVENT_NAME) Omissible<String> defaultEventName,
        @Parameter(description = "A set of tags") @JsonProperty(JSON_TAGS) Omissible<Set<String>> tags,
        @Parameter(description = "A set of scopes") @JsonProperty(JSON_SCOPES) Omissible<Set<BatchJobScope>> scopes,
        @Parameter(description = "Event columns that are using when dispatching BatchJob")
        @JsonProperty(JSON_EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @Parameter(description = "Columns that are used when validating BatchJob")
        @JsonProperty(JSON_COLUMNS) Omissible<Set<BatchJobColumnRequest>> columns,
        @Parameter(description = "Event data") @JsonProperty(JSON_EVENT_DATA) Omissible<Map<String, String>> eventData,
        @Parameter(description = "Data source") @JsonProperty(JSON_DATA_SOURCE) BatchJobDataSourceRequest dataSource) {
        this.name = name;
        this.eventName = eventName;
        this.defaultEventName = defaultEventName;
        this.tags = tags;
        this.eventData = eventData;
        this.dataSource = dataSource;
        this.eventColumns = eventColumns;
        this.columns = columns;
        this.scopes = scopes;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public Omissible<String> getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_DEFAULT_EVENT_NAME)
    public Omissible<String> getDefaultEventName() {
        return defaultEventName;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_SCOPES)
    public Omissible<Set<BatchJobScope>> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_EVENT_COLUMNS)
    public Omissible<Set<String>> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(JSON_COLUMNS)
    public Omissible<Set<BatchJobColumnRequest>> getColumns() {
        return columns;
    }

    @JsonProperty(JSON_EVENT_DATA)
    public Omissible<Map<String, String>> getEventData() {
        return eventData;
    }

    @JsonProperty(JSON_DATA_SOURCE)
    public BatchJobDataSourceRequest getDataSource() {
        return dataSource;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<String> name = Omissible.omitted();
        private Omissible<String> eventName = Omissible.omitted();
        private Omissible<String> defaultEventName = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<Set<BatchJobScope>> scopes = Omissible.omitted();
        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Set<BatchJobColumnRequest>> columns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();
        private BatchJobDataSourceRequest dataSource;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventName = Omissible.of(eventName);
            return this;
        }

        public Builder withDefaultEventName(String defaultEventName) {
            this.defaultEventName = Omissible.of(defaultEventName);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withScopes(Set<BatchJobScope> scopes) {
            this.scopes = Omissible.of(scopes);
            return this;
        }

        public Builder withEventColumns(Set<String> eventColumns) {
            this.eventColumns = Omissible.of(eventColumns);
            return this;
        }

        public Builder withColumns(Set<BatchJobColumnRequest> columns) {
            this.columns = Omissible.of(columns);
            return this;
        }

        public Builder withEventData(Map<String, String> data) {
            this.eventData = Omissible.of(data);
            return this;
        }

        public Builder withDataSource(BatchJobDataSourceRequest dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public BatchJobCreateRequest build() {
            return new BatchJobCreateRequest(name, eventName, defaultEventName, tags, scopes, eventColumns, columns,
                eventData, dataSource);
        }
    }
}
