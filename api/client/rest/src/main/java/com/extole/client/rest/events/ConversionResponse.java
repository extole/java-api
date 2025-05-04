package com.extole.client.rest.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.extole.common.lang.ToString;

@JsonPropertyOrder({"status", "message", "event_id"})
public class ConversionResponse {
    private final String status;
    private final String message;
    private final String eventId;

    public ConversionResponse(String message) {
        this("failure", message, null);
    }

    public ConversionResponse(String status, String message, String eventId) {
        this.status = status;
        this.message = message;
        this.eventId = eventId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("event_id")
    public String getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
