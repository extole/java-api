package com.extole.client.rest.v0;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ReviewStatusUpdateTriggerResponse {

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_MESSAGE = "message";
    private static final String PROPERTY_QUALITY_SCORE = "quality_score";
    private static final String PROPERTY_LOG_MESSAGES = "log_messages";

    private final String name;
    private final String message;
    private final QualityScore qualityScore;
    private final List<String> logMessages;

    @JsonCreator
    public ReviewStatusUpdateTriggerResponse(
        @JsonProperty(PROPERTY_NAME) String name,
        @JsonProperty(PROPERTY_MESSAGE) String message,
        @JsonProperty(PROPERTY_QUALITY_SCORE) QualityScore qualityScore,
        @JsonProperty(PROPERTY_LOG_MESSAGES) List<String> logMessages) {
        this.name = name;
        this.message = message;
        this.qualityScore = qualityScore;
        this.logMessages = logMessages != null ? Collections.unmodifiableList(logMessages) : Collections.emptyList();
    }

    @JsonProperty(PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(PROPERTY_MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(PROPERTY_QUALITY_SCORE)
    public QualityScore getQualityScore() {
        return qualityScore;
    }

    @JsonProperty(PROPERTY_LOG_MESSAGES)
    public List<String> getLogMessages() {
        return logMessages;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
