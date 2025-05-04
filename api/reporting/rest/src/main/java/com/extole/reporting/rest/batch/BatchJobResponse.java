package com.extole.reporting.rest.batch;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.batch.column.response.BatchJobColumnResponse;
import com.extole.reporting.rest.batch.data.source.response.BatchJobDataSourceResponse;

public class BatchJobResponse {
    private static final String JSON_ID = "id";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_DEFAULT_EVENT_NAME = "default_event_name";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_STARTED_DATE = "started_date";
    private static final String JSON_COMPLETED_DATE = "completed_date";
    private static final String JSON_JOB_STATUS = "status";
    private static final String JSON_NAME = "name";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_EVENT_DATA = "event_data";
    private static final String JSON_EVENT_COLUMNS = "event_columns";
    private static final String JSON_COLUMNS = "columns";
    private static final String JSON_DATA_SOURCE = "data_source";
    private static final String JSON_SUCCESS_ROWS = "success_rows";
    private static final String JSON_FAILED_ROWS = "failed_rows";
    private static final String JSON_TOPIC_NAME = "topic_name";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_ERROR_CODE = "error_code";
    private static final String JSON_ERROR_MESSAGE = "error_message";
    private static final String JSON_DEBUG_MESSAGE = "debug_message";

    private final String id;
    private final ZonedDateTime createdDate;
    private final Optional<ZonedDateTime> startedDate;
    private final Optional<ZonedDateTime> completedDate;
    private final Optional<String> eventName;
    private final String defaultEventName;
    private final BatchJobStatus status;
    private final String name;
    private final Set<String> tags;
    private final Map<String, String> eventData;
    private final Set<String> eventColumns;
    private final Set<BatchJobColumnResponse> columns;
    private final BatchJobDataSourceResponse dataSource;
    private final Set<BatchJobScope> scopes;
    private final Optional<Long> successRows;
    private final Optional<Long> failedRows;
    private final Optional<String> topicName;
    private final Optional<String> errorCode;
    private final Optional<String> errorMessage;
    private final Optional<String> debugMessage;

    public BatchJobResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_EVENT_NAME) Optional<String> eventName,
        @JsonProperty(JSON_DEFAULT_EVENT_NAME) String defaultEventName,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_STARTED_DATE) Optional<ZonedDateTime> startedDate,
        @JsonProperty(JSON_COMPLETED_DATE) Optional<ZonedDateTime> completedDate,
        @JsonProperty(JSON_JOB_STATUS) BatchJobStatus status,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_TAGS) Set<String> tags,
        @JsonProperty(JSON_EVENT_DATA) Map<String, String> eventData,
        @JsonProperty(JSON_EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(JSON_COLUMNS) Set<BatchJobColumnResponse> columns,
        @JsonProperty(JSON_DATA_SOURCE) BatchJobDataSourceResponse dataSource,
        @JsonProperty(JSON_SUCCESS_ROWS) Optional<Long> successRows,
        @JsonProperty(JSON_FAILED_ROWS) Optional<Long> failedRows,
        @JsonProperty(JSON_TOPIC_NAME) Optional<String> topicName,
        @JsonProperty(JSON_SCOPES) Set<BatchJobScope> scopes,
        @JsonProperty(JSON_ERROR_CODE) Optional<String> errorCode,
        @JsonProperty(JSON_ERROR_MESSAGE) Optional<String> errorMessage,
        @JsonProperty(JSON_DEBUG_MESSAGE) Optional<String> debugMessage) {
        this.id = id;
        this.eventName = eventName;
        this.defaultEventName = defaultEventName;
        this.createdDate = createdDate;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
        this.status = status;
        this.name = name;
        this.tags = tags;
        this.eventData = eventData;
        this.eventColumns = eventColumns;
        this.columns = columns;
        this.dataSource = dataSource;
        this.successRows = successRows;
        this.failedRows = failedRows;
        this.topicName = topicName;
        this.scopes = scopes;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.debugMessage = debugMessage;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public Optional<String> getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_DEFAULT_EVENT_NAME)
    public String getDefaultEventName() {
        return defaultEventName;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_STARTED_DATE)
    public Optional<ZonedDateTime> getStartedDate() {
        return startedDate;
    }

    @JsonProperty(JSON_COMPLETED_DATE)
    public Optional<ZonedDateTime> getCompletedDate() {
        return completedDate;
    }

    @JsonProperty(JSON_JOB_STATUS)
    public BatchJobStatus getStatus() {
        return status;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_EVENT_DATA)
    public Map<String, String> getEventData() {
        return eventData;
    }

    @JsonProperty(JSON_EVENT_COLUMNS)
    public Set<String> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(JSON_COLUMNS)
    public Set<BatchJobColumnResponse> getColumns() {
        return columns;
    }

    @JsonProperty(JSON_DATA_SOURCE)
    public BatchJobDataSourceResponse getDataSource() {
        return dataSource;
    }

    @JsonProperty(JSON_SUCCESS_ROWS)
    public Optional<Long> getSuccessRows() {
        return successRows;
    }

    @JsonProperty(JSON_FAILED_ROWS)
    public Optional<Long> getFailedRows() {
        return failedRows;
    }

    @JsonProperty(JSON_TOPIC_NAME)
    public Optional<String> getTopicName() {
        return topicName;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<BatchJobScope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_ERROR_CODE)
    public Optional<String> getErrorCode() {
        return errorCode;
    }

    @JsonProperty(JSON_ERROR_MESSAGE)
    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty(JSON_DEBUG_MESSAGE)
    public Optional<String> getDebugMessage() {
        return debugMessage;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
