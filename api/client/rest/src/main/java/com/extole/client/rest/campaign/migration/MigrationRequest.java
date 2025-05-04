package com.extole.client.rest.campaign.migration;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public final class MigrationRequest {
    private static final String VALUE_OVERRIDES = "value_overrides";
    private static final String PUBLISH = "publish";
    private static final String MESSAGE = "message";

    private final Omissible<Map<String, String>> valueOverrides;
    private final Omissible<Boolean> publish;
    private final Omissible<Optional<String>> message;

    @JsonCreator
    private MigrationRequest(@JsonProperty(VALUE_OVERRIDES) Omissible<Map<String, String>> valueOverrides,
        @JsonProperty(PUBLISH) Omissible<Boolean> publish,
        @JsonProperty(MESSAGE) Omissible<Optional<String>> message) {
        this.valueOverrides = valueOverrides;
        this.publish = publish;
        this.message = message;
    }

    @JsonProperty(VALUE_OVERRIDES)
    public Omissible<Map<String, String>> getValueOverrides() {
        return valueOverrides;
    }

    @JsonProperty(PUBLISH)
    public Omissible<Boolean> getPublish() {
        return publish;
    }

    @JsonProperty(MESSAGE)
    public Omissible<Optional<String>> getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Omissible<Map<String, String>> valueOverrides = Omissible.omitted();
        private Omissible<Boolean> publish = Omissible.omitted();
        private Omissible<Optional<String>> message = Omissible.omitted();

        public Builder valueOverrides(Map<String, String> valueOverrides) {
            this.valueOverrides = Omissible.of(valueOverrides);
            return this;
        }

        public Builder withPublish(Boolean publish) {
            this.publish = Omissible.of(publish);
            return this;
        }

        public Builder withMessage(String message) {
            this.message = Omissible.of(Optional.ofNullable(message));
            return this;
        }

        public MigrationRequest build() {
            return new MigrationRequest(valueOverrides, publish, message);
        }
    }
}
