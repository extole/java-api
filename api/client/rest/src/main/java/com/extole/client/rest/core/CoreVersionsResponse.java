package com.extole.client.rest.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoreVersionsResponse {
    private static final String VERSIONS = "versions";

    private final List<String> versions;

    @JsonCreator
    public CoreVersionsResponse(@JsonProperty(VERSIONS) List<String> versions) {
        this.versions = versions;
    }

    @JsonProperty(VERSIONS)
    public List<String> getVersions() {
        return versions;
    }
}
