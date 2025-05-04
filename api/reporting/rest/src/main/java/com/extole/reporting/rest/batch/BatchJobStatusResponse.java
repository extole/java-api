package com.extole.reporting.rest.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchJobStatusResponse {
    private static final String JSON_EVENTS_PROCESSED = "events_processed";
    private static final String JSON_EVENTS_TO_PROCESS = "events_to_process";

    private final long eventsProcessed;
    private final long eventsToProcess;

    public BatchJobStatusResponse(
        @JsonProperty(JSON_EVENTS_PROCESSED) long eventsProcessed,
        @JsonProperty(JSON_EVENTS_TO_PROCESS) long eventsToProcess) {
        this.eventsProcessed = eventsProcessed;
        this.eventsToProcess = eventsToProcess;
    }

    @JsonProperty(JSON_EVENTS_PROCESSED)
    public long getEventsProcessed() {
        return eventsProcessed;
    }

    @JsonProperty(JSON_EVENTS_TO_PROCESS)
    public long getEventsToProcess() {
        return eventsToProcess;
    }
}
