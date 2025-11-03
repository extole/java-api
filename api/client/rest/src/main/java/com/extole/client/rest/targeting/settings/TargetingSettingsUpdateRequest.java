package com.extole.client.rest.targeting.settings;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class TargetingSettingsUpdateRequest {

    private static final String JSON_VERSION = "version";
    private static final String JSON_DRY_RUN_VERSION = "dry_run_version";

    private final Omissible<TargetingVersion> version;
    private final Omissible<Optional<TargetingVersion>> dryRunVersion;

    @JsonCreator
    public TargetingSettingsUpdateRequest(
        @JsonProperty(JSON_VERSION) Omissible<TargetingVersion> version,
        @JsonProperty(JSON_DRY_RUN_VERSION) Omissible<Optional<TargetingVersion>> dryRunVersion) {
        this.version = version;
        this.dryRunVersion = dryRunVersion;
    }

    @JsonProperty(JSON_VERSION)
    public Omissible<TargetingVersion> getVersion() {
        return version;
    }

    @JsonProperty(JSON_DRY_RUN_VERSION)
    public Omissible<Optional<TargetingVersion>> getDryRunVersion() {
        return dryRunVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Omissible<TargetingVersion> version = Omissible.omitted();
        private Omissible<Optional<TargetingVersion>> dryRunVersion = Omissible.omitted();

        public Builder withVersion(TargetingVersion version) {
            this.version = Omissible.of(version);
            return this;
        }

        public Builder withDryRunVersion(TargetingVersion dryRunVersion) {
            this.dryRunVersion = Omissible.of(Optional.of(dryRunVersion));
            return this;
        }

        public Builder clearDryRunVersion() {
            this.dryRunVersion = Omissible.of(Optional.empty());
            return this;
        }

        public TargetingSettingsUpdateRequest build() {
            return new TargetingSettingsUpdateRequest(version, dryRunVersion);
        }
    }
}
