package com.extole.client.rest.events.v4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientConsumerEventV4Response {

    @Deprecated // TODO remove deprecated members ENG-12142
    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_EVENT_ID = "event_id";
    private static final String JSON_INPUT_EVENT_ID = "input_event_id";
    private static final String JSON_ID = "id";

    @Deprecated // TODO remove deprecated members ENG-12142
    private final String pollingId;
    @Deprecated // TODO remove deprecated members ENG-12142
    private final String eventId;
    private final String id;

    public ClientConsumerEventV4Response(
        @Deprecated // TODO remove deprecated members ENG-12142
        @JsonProperty(JSON_POLLING_ID) String pollingId,
        @Deprecated // TODO remove deprecated members ENG-12142
        @JsonProperty(JSON_EVENT_ID) String eventId,
        @JsonProperty(JSON_ID) String id) {
        this.pollingId = pollingId;
        this.eventId = eventId;
        this.id = id;
    }

    @Deprecated // TODO remove deprecated members ENG-12142
    @JsonProperty(JSON_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @Deprecated // TODO remove deprecated members ENG-12142
    @JsonProperty(JSON_EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @JsonProperty(JSON_INPUT_EVENT_ID)
    public String getInputEventId() {
        return id;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

}
