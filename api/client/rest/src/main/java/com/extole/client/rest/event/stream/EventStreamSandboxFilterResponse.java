package com.extole.client.rest.event.stream;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class EventStreamSandboxFilterResponse
    extends EventStreamFilterResponse {
    public static final String TYPE_SANDBOX = "SANDBOX";

    private static final String SANDBOXES = "sandboxes";
    private static final String CONTAINERS = "containers";

    private final List<String> sandboxes;
    private final List<String> containers;

    public EventStreamSandboxFilterResponse(@JsonProperty(TYPE) EventFilterType type,
        @JsonProperty(ID) Id<?> id,
        @JsonProperty(SANDBOXES) List<String> sandboxes,
        @JsonProperty(CONTAINERS) List<String> containers) {
        super(type, id);
        this.sandboxes = sandboxes;
        this.containers = containers;
    }

    @JsonProperty(SANDBOXES)
    public List<String> getSandboxes() {
        return sandboxes;
    }

    @JsonProperty(CONTAINERS)
    public List<String> getContainers() {
        return containers;
    }
}
