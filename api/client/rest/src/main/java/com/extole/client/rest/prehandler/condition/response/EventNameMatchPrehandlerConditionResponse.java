package com.extole.client.rest.prehandler.condition.response;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;

@Schema(description = "Condition that checks the event name.")
public class EventNameMatchPrehandlerConditionResponse extends PrehandlerConditionResponse {
    static final String TYPE = "EVENT_NAME_MATCH";

    private static final String JSON_EVENT_NAMES = "event_names";

    private final Set<String> eventNames;

    @JsonCreator
    public EventNameMatchPrehandlerConditionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_EVENT_NAMES) Set<String> eventNames) {
        super(id, PrehandlerConditionType.EVENT_NAME_MATCH);
        this.eventNames =
            eventNames != null ? Collections.unmodifiableSet(new HashSet<>(eventNames)) : Collections.emptySet();
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_EVENT_NAMES)
    @Schema(nullable = false)
    public Set<String> getEventNames() {
        return this.eventNames;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Set<String> eventNames;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withEventNames(Set<String> eventNames) {
            this.eventNames = eventNames;
            return this;
        }

        public EventNameMatchPrehandlerConditionResponse build() {
            return new EventNameMatchPrehandlerConditionResponse(id, eventNames);
        }
    }
}
