package com.extole.reporting.rest.report.access;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ReportSharedTokenCreateRequest {

    private static final String JSON_DURATION_SECONDS = "duration_seconds";
    private final Omissible<Long> durationSeconds;

    @JsonCreator
    ReportSharedTokenCreateRequest(
        @JsonProperty(JSON_DURATION_SECONDS) Omissible<Long> durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    @JsonProperty(JSON_DURATION_SECONDS)
    public Omissible<Long> getDurationSeconds() {
        return durationSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Long> durationSeconds = Omissible.omitted();

        private Builder() {
        }

        public Builder withDurationSeconds(long durationSeconds) {
            this.durationSeconds = Omissible.of(Long.valueOf(durationSeconds));
            return this;
        }

        public ReportSharedTokenCreateRequest build() {
            return new ReportSharedTokenCreateRequest(durationSeconds);
        }
    }
}
