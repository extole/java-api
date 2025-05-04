package com.extole.client.rest.prehandler.condition.request;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;

@Schema(description = "Condition that checks the event name.")
public class EventNameMatchPrehandlerConditionRequest extends PrehandlerConditionRequest {
    static final String TYPE = "EVENT_NAME_MATCH";

    private static final String JSON_EVENT_NAMES = "event_names";

    private final Set<String> eventNames;

    @JsonCreator
    public EventNameMatchPrehandlerConditionRequest(
        @JsonProperty(JSON_EVENT_NAMES) Set<String> eventNames) {
        super(PrehandlerConditionType.EVENT_NAME_MATCH);
        this.eventNames =
            eventNames != null ? Collections.unmodifiableSet(new HashSet<>(eventNames)) : Collections.emptySet();
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_EVENT_NAMES)
    @Schema(required = true, nullable = false,
        description = "Condition evaluates to true if the event name is present in this set."
            + " Event names are not case sensitive.")
    public Set<String> getEventNames() {
        return this.eventNames;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Set<String> eventNames;

        public Builder withEventNames(Set<String> eventNames) {
            this.eventNames = eventNames;
            return this;
        }

        public EventNameMatchPrehandlerConditionRequest build() {
            return new EventNameMatchPrehandlerConditionRequest(eventNames);
        }
    }
}
