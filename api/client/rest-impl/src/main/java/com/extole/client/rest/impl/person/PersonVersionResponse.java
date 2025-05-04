package com.extole.client.rest.impl.person;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonVersionResponse {
    private static final String JSON_RUNTIME_VERSION = "runtime_version";
    private static final String JSON_PERSISTED_VERSION = "persisted_version";

    private final String runtimeVersion;
    private final Optional<String> persistedVersion;

    @JsonCreator
    public PersonVersionResponse(
        @JsonProperty(JSON_RUNTIME_VERSION) String runtimeVersion,
        @JsonProperty(JSON_PERSISTED_VERSION) Optional<String> persistedVersion) {
        this.runtimeVersion = runtimeVersion;
        this.persistedVersion = persistedVersion;
    }

    @JsonProperty(JSON_RUNTIME_VERSION)
    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    @JsonProperty(JSON_PERSISTED_VERSION)
    public Optional<String> getPersistedVersion() {
        return persistedVersion;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
