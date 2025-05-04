package com.extole.reporting.rest.file.assets;

import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class FileAssetResponse {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String STATUS = "status";
    private static final String REVIEW_STATUS = "review_status";
    private static final String TAGS = "tags";
    private static final String FORMAT = "format";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String USER_ID = "user_id";
    private static final String SIZE = "size";

    private final String id;
    private final String name;
    private final FileAssetStatus status;
    private final FileAssetReviewStatus reviewStatus;
    private final Set<String> tags;
    private final String format;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final String userId;
    private final long size;

    public FileAssetResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(STATUS) FileAssetStatus status,
        @JsonProperty(REVIEW_STATUS) FileAssetReviewStatus reviewStatus,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(FORMAT) String format,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(SIZE) long size) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.reviewStatus = reviewStatus;
        this.tags = tags;
        this.format = format;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.userId = userId;
        this.size = size;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(STATUS)
    public FileAssetStatus getStatus() {
        return status;
    }

    @JsonProperty(REVIEW_STATUS)
    public FileAssetReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(FORMAT)
    public String getFormat() {
        return format;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(SIZE)
    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
