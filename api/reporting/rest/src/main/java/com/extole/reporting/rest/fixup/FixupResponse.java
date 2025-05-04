package com.extole.reporting.rest.fixup;

import java.time.ZonedDateTime;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.reporting.rest.fixup.filter.FixupFilterResponse;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationResponse;

public class FixupResponse {
    private static final String JSON_ID = "id";
    private static final String JSON_DATA_SOURCE = "data_source";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_FILTER = "filter";
    private static final String JSON_TRANSFORMATION = "transformation";
    private static final String JSON_EXECUTIONS = "executions";
    private static final String JSON_CREATED_DATE = "created_date";

    private final String id;
    private final FixupDataSource dataSource;
    private final String name;
    private final String description;
    private final FixupFilterResponse filter;
    private final FixupTransformationResponse transformation;
    private final List<FixupExecutionResponse> executions;
    private final ZonedDateTime createdDate;

    public FixupResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_DATA_SOURCE) FixupDataSource dataSource,
        @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DESCRIPTION) String description,
        @Nullable @JsonProperty(JSON_FILTER) FixupFilterResponse filter,
        @Nullable @JsonProperty(JSON_TRANSFORMATION) FixupTransformationResponse transformation,
        @JsonProperty(JSON_EXECUTIONS) List<FixupExecutionResponse> executions,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate) {
        this.id = id;
        this.dataSource = dataSource;
        this.name = name;
        this.description = description;
        this.filter = filter;
        this.transformation = transformation;
        this.executions = ImmutableList.copyOf(executions);
        this.createdDate = createdDate;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_DATA_SOURCE)
    public FixupDataSource getDataSource() {
        return dataSource;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(JSON_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Nullable
    @JsonProperty(JSON_FILTER)
    public FixupFilterResponse getFilter() {
        return filter;
    }

    @Nullable
    @JsonProperty(JSON_TRANSFORMATION)
    public FixupTransformationResponse getTransformation() {
        return transformation;
    }

    @JsonProperty(JSON_EXECUTIONS)
    public List<FixupExecutionResponse> getExecutions() {
        return executions;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }
}
