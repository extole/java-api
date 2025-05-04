package com.extole.client.rest.v0;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class ReviewStatusUpdateResponse {
    private static final String PROPERTY_UPDATE_ID = "update_id";
    private static final String PROPERTY_NOTE = "note";
    private static final String PROPERTY_CAUSE_ID = "cause_id";
    private static final String PROPERTY_REVIEW_STATUS = "review_status";
    private static final String PROPERTY_REVIEW_STATUS_MESSAGE = "review_status_message";
    private static final String PROPERTY_UPDATE_DATE = "update_date";
    private static final String PROPERTY_CAUSE_TYPE = "cause_type";
    private static final String PROPERTY_DEBUG_MESSAGE = "debug_message";
    private static final String PROPERTY_TRIGGER_RESULTS = "trigger_results";
    private static final String PROPERTY_DATA = "data";

    private final String updateId;
    private final String note;
    private final String causeId;
    private final ReviewStatus reviewStatus;
    private final String reviewStatusMessage;
    private final Long updateDate;
    private final ReviewStatusCauseType causeType;
    private final String debugMessage;
    private final List<ReviewStatusUpdateTriggerResponse> triggerResults;
    private final Map<String, String> data;

    @JsonCreator
    public ReviewStatusUpdateResponse(
        @JsonProperty(PROPERTY_UPDATE_ID) String updateId,
        @JsonProperty(PROPERTY_NOTE) String note,
        @JsonProperty(PROPERTY_CAUSE_ID) String causeId,
        @JsonProperty(PROPERTY_REVIEW_STATUS) ReviewStatus reviewStatus,
        @JsonProperty(PROPERTY_REVIEW_STATUS_MESSAGE) String reviewStatusMessage,
        @JsonProperty(PROPERTY_UPDATE_DATE) Long updateDate,
        @JsonProperty(PROPERTY_CAUSE_TYPE) ReviewStatusCauseType causeType,
        @JsonProperty(PROPERTY_DEBUG_MESSAGE) String debugMessage,
        @JsonProperty(PROPERTY_TRIGGER_RESULTS) List<ReviewStatusUpdateTriggerResponse> triggerResults,
        @JsonProperty(PROPERTY_DATA) Map<String, String> data) {
        this.updateId = updateId;
        this.note = note;
        this.causeId = causeId;
        this.reviewStatus = reviewStatus;
        this.reviewStatusMessage = reviewStatusMessage;
        this.updateDate = updateDate;
        this.causeType = causeType;
        this.debugMessage = debugMessage;
        this.triggerResults = triggerResults != null ? ImmutableList.copyOf(triggerResults) : Collections.emptyList();
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(PROPERTY_UPDATE_ID)
    public String getUpdateId() {
        return updateId;
    }

    @JsonProperty(PROPERTY_NOTE)
    public String getNote() {
        return note;
    }

    @JsonProperty(PROPERTY_CAUSE_ID)
    public String getCauseId() {
        return causeId;
    }

    @JsonProperty(PROPERTY_REVIEW_STATUS)
    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    @JsonProperty(PROPERTY_REVIEW_STATUS_MESSAGE)
    public String getReviewStatusMessage() {
        return reviewStatusMessage;
    }

    @JsonProperty(PROPERTY_UPDATE_DATE)
    public Long getUpdateDate() {
        return updateDate;
    }

    @JsonProperty(PROPERTY_CAUSE_TYPE)
    public ReviewStatusCauseType getCauseType() {
        return causeType;
    }

    @JsonProperty(PROPERTY_DEBUG_MESSAGE)
    public String getDebugMessage() {
        return debugMessage;
    }

    @JsonProperty(PROPERTY_TRIGGER_RESULTS)
    public List<ReviewStatusUpdateTriggerResponse> getTriggerResults() {
        return triggerResults;
    }

    @JsonProperty(PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
