package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class EventStreamSandboxFilterUpdateRequest
    extends EventStreamFilterUpdateRequest {
    public static final String TYPE_SANDBOX = "SANDBOX";

    private static final String SANDBOXES = "sandboxes";
    private static final String CONTAINERS = "containers";

    private final Omissible<List<String>> sandboxes;
    private final Omissible<List<String>> containers;

    public EventStreamSandboxFilterUpdateRequest(
        @JsonProperty(SANDBOXES) Omissible<List<String>> sandboxes,
        @JsonProperty(CONTAINERS) Omissible<List<String>> containers) {
        super(EventFilterType.SANDBOX);
        this.sandboxes = sandboxes;
        this.containers = containers;
    }

    @JsonProperty(SANDBOXES)
    public Omissible<List<String>> getSandboxes() {
        return sandboxes;
    }

    @JsonProperty(CONTAINERS)
    public Omissible<List<String>> getContainers() {
        return containers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<List<String>> sandboxes = Omissible.omitted();
        private Omissible<List<String>> containers = Omissible.omitted();

        private Builder() {
        }

        public Builder withSandboxes(List<String> sandboxes) {
            this.sandboxes = Omissible.of(sandboxes);
            return this;
        }

        public Builder withContainers(List<String> containers) {
            this.containers = Omissible.of(containers);
            return this;
        }

        public EventStreamSandboxFilterUpdateRequest build() {
            return new EventStreamSandboxFilterUpdateRequest(sandboxes, containers);
        }
    }
}
