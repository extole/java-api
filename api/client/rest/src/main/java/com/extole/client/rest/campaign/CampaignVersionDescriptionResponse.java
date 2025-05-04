package com.extole.client.rest.campaign;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CampaignVersionDescriptionResponse {
    private static final String VERSION = "version";
    private static final String PARENT_VERSION = "parent_version";
    private static final String CREATE_DATE = "created_date";
    private static final String PUBLISHED_DATE = "published_date";
    private static final String LAST_PUBLISHED_DATE = "last_published_date";
    private static final String START_DATE = "start_date";
    private static final String STOP_DATE = "stop_date";
    private static final String PAUSE_DATE = "pause_date";
    private static final String END_DATE = "end_date";
    private static final String USER_ID = "user_id";
    private static final String EDITOR_ID = "editor_id";
    private static final String EDITOR_TYPE = "editor_type";
    private static final String MESSAGE = "message";

    private final String version;
    private final Optional<String> parentVersion;
    private final ZonedDateTime createdDate;
    private final Optional<ZonedDateTime> publishedDate;
    private final Optional<ZonedDateTime> lastPublishedDate;
    private final Optional<ZonedDateTime> startDate;
    private final Optional<ZonedDateTime> stopDate;
    private final Optional<ZonedDateTime> pauseDate;
    private final Optional<ZonedDateTime> endDate;
    private final Optional<String> message;
    private final String userId;
    private final String editorId;
    private final String editorType;

    public CampaignVersionDescriptionResponse(@JsonProperty(VERSION) String version,
        @JsonProperty(PARENT_VERSION) Optional<String> parentVersion,
        @JsonProperty(CREATE_DATE) ZonedDateTime createdDate,
        @JsonProperty(PUBLISHED_DATE) Optional<ZonedDateTime> publishedDate,
        @JsonProperty(LAST_PUBLISHED_DATE) Optional<ZonedDateTime> lastPublishedDate,
        @JsonProperty(START_DATE) Optional<ZonedDateTime> startDate,
        @JsonProperty(STOP_DATE) Optional<ZonedDateTime> stopDate,
        @JsonProperty(PAUSE_DATE) Optional<ZonedDateTime> pauseDate,
        @JsonProperty(END_DATE) Optional<ZonedDateTime> endDate,
        @JsonProperty(MESSAGE) Optional<String> message,
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(EDITOR_ID) String editorId,
        @JsonProperty(EDITOR_TYPE) String editorType) {
        this.version = version;
        this.parentVersion = parentVersion;
        this.createdDate = createdDate;
        this.publishedDate = publishedDate;
        this.lastPublishedDate = lastPublishedDate;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.pauseDate = pauseDate;
        this.endDate = endDate;
        this.message = message;
        this.userId = userId;
        this.editorId = editorId;
        this.editorType = editorType;
    }

    @JsonProperty(VERSION)
    public String getVersion() {
        return version;
    }

    @JsonProperty(PARENT_VERSION)
    public Optional<String> getParentVersion() {
        return parentVersion;
    }

    @JsonProperty(CREATE_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(PUBLISHED_DATE)
    public Optional<ZonedDateTime> getPublishDate() {
        return publishedDate;
    }

    @JsonProperty(LAST_PUBLISHED_DATE)
    public Optional<ZonedDateTime> getLastPublishedDate() {
        return lastPublishedDate;
    }

    @JsonProperty(START_DATE)
    public Optional<ZonedDateTime> getStartDate() {
        return startDate;
    }

    @JsonProperty(STOP_DATE)
    public Optional<ZonedDateTime> getStopDate() {
        return stopDate;
    }

    @JsonProperty(PAUSE_DATE)
    public Optional<ZonedDateTime> getPauseDate() {
        return pauseDate;
    }

    @JsonProperty(END_DATE)
    public Optional<ZonedDateTime> getEndDate() {
        return endDate;
    }

    @JsonProperty(MESSAGE)
    public Optional<String> getMessage() {
        return message;
    }

    @Deprecated // TODO cannot guarantee that is userId, can be managedTokenId or personId for old data or - ENG-18121
    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(EDITOR_ID)
    public String getEditorId() {
        return editorId;
    }

    @JsonProperty(EDITOR_TYPE)
    public String getEditorType() {
        return editorType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
