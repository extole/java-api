package com.extole.reporting.rest.processing_lag;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ProcessingStageStatusResponse {
    private static final String JSON_PROCESSING_STAGE = "processing_stage";
    private static final String JSON_PROCESSED_UP_TO_TIME = "processed_up_to_time";
    private static final String JSON_LAST_UPDATED = "last_updated";

    private final ProcessingStage processingStage;
    private final Instant processedUpToTime;
    private final Instant lastUpdated;

    @JsonCreator
    public ProcessingStageStatusResponse(@JsonProperty(JSON_PROCESSING_STAGE) ProcessingStage processingStage,
        @JsonProperty(JSON_PROCESSED_UP_TO_TIME) Instant processedUpToTime,
        @JsonProperty(JSON_LAST_UPDATED) Instant lastUpdated) {
        this.processingStage = processingStage;
        this.processedUpToTime = processedUpToTime;
        this.lastUpdated = lastUpdated;
    }

    @JsonProperty(JSON_PROCESSING_STAGE)
    public ProcessingStage getProcessingStage() {
        return processingStage;
    }

    @JsonProperty(JSON_PROCESSED_UP_TO_TIME)
    public Instant getProcessedUpToTime() {
        return processedUpToTime;
    }

    @JsonProperty(JSON_LAST_UPDATED)
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
