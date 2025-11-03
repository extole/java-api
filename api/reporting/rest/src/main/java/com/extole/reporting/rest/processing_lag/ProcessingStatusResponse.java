package com.extole.reporting.rest.processing_lag;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ProcessingStatusResponse {
    private static final String JSON_PROCESSING_STAGES = "processing_stages";

    private final Map<ProcessingStage, Optional<ProcessingStageStatusResponse>> processingStages;

    @JsonCreator
    public ProcessingStatusResponse(@JsonProperty(JSON_PROCESSING_STAGES) Map<ProcessingStage,
        Optional<ProcessingStageStatusResponse>> processingStages) {
        this.processingStages = Map.copyOf(processingStages);
    }

    @JsonProperty(JSON_PROCESSING_STAGES)
    public Map<ProcessingStage, Optional<ProcessingStageStatusResponse>> getProcessingStages() {
        return processingStages;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
