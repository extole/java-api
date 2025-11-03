package com.extole.common.migration.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.migration.VersionedPojo;

public final class VersionedPojoImpl implements VersionedPojo {

    private static final String SCHEMA_VERSION = "schema_version";

    private final int schemaVersion;

    @JsonCreator
    public VersionedPojoImpl(@JsonProperty(SCHEMA_VERSION) int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    @Override
    @JsonProperty(SCHEMA_VERSION)
    public int getSchemaVersion() {
        return schemaVersion;
    }

}
