package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class EventStreamApplicationTypeFilterCreateRequest
    extends EventStreamFilterCreateRequest {
    public static final String TYPE_APPLICATION_TYPE = "APPLICATION_TYPE";

    private static final String APP_TYPES = "app_types";

    private final Omissible<List<String>> applicationTypes;

    public EventStreamApplicationTypeFilterCreateRequest(
        @JsonProperty(APP_TYPES) Omissible<List<String>> applicationTypes) {
        super(EventFilterType.APPLICATION_TYPE);
        this.applicationTypes = applicationTypes;
    }

    @JsonProperty(APP_TYPES)
    public Omissible<List<String>> getApplicationTypes() {
        return applicationTypes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<List<String>> applicationTypes = Omissible.omitted();

        private Builder() {
        }

        public Builder withApplicationType(Omissible<List<String>> applicationType) {
            this.applicationTypes = applicationType;
            return this;
        }

        public EventStreamApplicationTypeFilterCreateRequest build() {
            return new EventStreamApplicationTypeFilterCreateRequest(applicationTypes);
        }
    }
}
