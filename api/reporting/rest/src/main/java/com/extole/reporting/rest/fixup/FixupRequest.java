package com.extole.reporting.rest.fixup;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FixupRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_DATA_SOURCE = "data_source";
    private static final String JSON_DESCRIPTION = "description";

    private final String name;
    private final FixupDataSource dataSource;
    private final String description;

    @JsonCreator
    public FixupRequest(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DATA_SOURCE) FixupDataSource dataSource,
        @Nullable @JsonProperty(JSON_DESCRIPTION) String description) {
        this.name = name;
        this.dataSource = dataSource;
        this.description = description;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DATA_SOURCE)
    public FixupDataSource getDataSource() {
        return dataSource;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public String getDescription() {
        return description;
    }
}
