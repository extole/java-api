package com.extole.client.rest.targeting.settings;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetingSettingsResponse {

    private static final String JSON_VERSION = "version";
    private static final String JSON_DRY_RUN_VERSION = "dry_run_version";

    private final TargetingVersion version;
    private final Optional<TargetingVersion> dryRunVersion;

    @JsonCreator
    public TargetingSettingsResponse(
        @JsonProperty(JSON_VERSION) TargetingVersion version,
        @JsonProperty(JSON_DRY_RUN_VERSION) Optional<TargetingVersion> dryRunVersion) {
        this.version = version;
        this.dryRunVersion = dryRunVersion;
    }

    @JsonProperty(JSON_VERSION)
    public TargetingVersion getVersion() {
        return version;
    }

    @JsonProperty(JSON_DRY_RUN_VERSION)
    public Optional<TargetingVersion> getDryRunVersion() {
        return dryRunVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TargetingVersion version;
        private Optional<TargetingVersion> dryRunVersion = Optional.empty();

        public Builder withVersion(TargetingVersion version) {
            this.version = version;
            return this;
        }

        public Builder withDryRunVersion(TargetingVersion dryRunVersion) {
            this.dryRunVersion = Optional.of(dryRunVersion);
            return this;
        }

        public TargetingSettingsResponse build() {
            return new TargetingSettingsResponse(version, dryRunVersion);
        }
    }
}
