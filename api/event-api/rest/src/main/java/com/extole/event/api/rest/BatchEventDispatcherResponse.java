package com.extole.event.api.rest;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchEventDispatcherResponse {
    private static final String EVENT_ID = "event_id";
    private static final String REQUEST = "request";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String STATUS = "status";

    private final EventDispatcherRequest request;
    private final String eventId;
    private final String errorMessage;
    private final Status status;

    @JsonCreator
    public BatchEventDispatcherResponse(
        @JsonProperty(REQUEST) EventDispatcherRequest request,
        @JsonProperty(STATUS) Status status,
        @Nullable @JsonProperty(EVENT_ID) String eventId,
        @Nullable @JsonProperty(ERROR_MESSAGE) String errorMessage) {
        this.request = request;
        this.eventId = eventId;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    @JsonProperty(REQUEST)
    public EventDispatcherRequest getRequest() {
        return request;
    }

    @JsonProperty(STATUS)
    public Status getStatus() {
        return status;
    }

    @Nullable
    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @Nullable
    @JsonProperty(ERROR_MESSAGE)
    public String getErrorMessage() {
        return errorMessage;
    }

    public enum Status {
        SUCCEEDED, FAILED
    }

    public static final BatchClientConsumerEventResponseBuilder newBuilder(EventDispatcherRequest request) {
        return new BatchClientConsumerEventResponseBuilder(request);
    }

    public static final class BatchClientConsumerEventResponseBuilder {
        private EventDispatcherRequest request;
        private String eventId;
        private String errorMessage;
        private Status status;

        private BatchClientConsumerEventResponseBuilder(EventDispatcherRequest request) {
            this.request = request;
        }

        public BatchClientConsumerEventResponseBuilder withError(String message) {
            this.errorMessage = message;
            this.status = Status.FAILED;
            return this;
        }

        public BatchClientConsumerEventResponseBuilder withSuccess(String eventId) {
            this.eventId = eventId;
            this.status = Status.SUCCEEDED;
            return this;
        }

        public BatchEventDispatcherResponse build() {
            return new BatchEventDispatcherResponse(request, status, eventId, errorMessage);
        }
    }
}
