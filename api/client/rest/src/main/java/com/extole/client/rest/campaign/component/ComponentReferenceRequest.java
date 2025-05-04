package com.extole.client.rest.campaign.component;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public class ComponentReferenceRequest {

    private static final String COMPONENT_ID = "component_id";
    private static final String SOCKET_NAMES = "socket_names";

    private final Id<ComponentResponse> componentId;
    private final Omissible<List<String>> socketNames;

    public ComponentReferenceRequest(@JsonProperty(COMPONENT_ID) Id<ComponentResponse> componentId,
        @JsonProperty(SOCKET_NAMES) Omissible<List<String>> socketNames) {
        this.componentId = componentId;
        this.socketNames = socketNames;
    }

    @JsonProperty(COMPONENT_ID)
    public Id<ComponentResponse> getComponentId() {
        return componentId;
    }

    @JsonProperty(SOCKET_NAMES)
    public Omissible<List<String>> getSocketNames() {
        return socketNames;
    }

    public static <CALLER> Builder<CALLER> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER> {

        private final CALLER caller;
        private Id<ComponentResponse> componentId;
        private Omissible<List<String>> socketNames = Omissible.omitted();

        private Builder(CALLER caller) {
            this.caller = caller;
        }

        public Builder<CALLER> withComponentId(Id<ComponentResponse> componentId) {
            this.componentId = componentId;
            return this;
        }

        public Builder<CALLER> withSocketNames(List<String> socketNames) {
            this.socketNames = Omissible.of(socketNames);
            return this;
        }

        public ComponentReferenceRequest build() {
            return new ComponentReferenceRequest(componentId, socketNames);
        }

        public CALLER done() {
            return caller;
        }

    }

}
