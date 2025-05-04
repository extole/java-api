package com.extole.reporting.rest.report.runner;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class PauseInfoRequest {
    private static final String JSON_PAUSED = "paused";
    private static final String JSON_DESCRIPTION = "description";
    private final Omissible<Boolean> paused;
    private final Omissible<Optional<String>> description;

    public PauseInfoRequest(@JsonProperty(JSON_PAUSED) Omissible<Boolean> paused,
        @JsonProperty(JSON_DESCRIPTION) Omissible<Optional<String>> description) {
        this.paused = paused;
        this.description = description;
    }

    @JsonProperty(JSON_PAUSED)
    public Omissible<Boolean> getPaused() {
        return paused;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Boolean> paused = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();

        private Builder() {
        }

        public Builder withPaused(Boolean paused) {
            this.paused = Omissible.of(paused);
            return this;
        }

        public Builder withDescription(Optional<String> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public PauseInfoRequest build() {
            return new PauseInfoRequest(paused, description);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
