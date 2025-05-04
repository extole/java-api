package com.extole.reporting.rest.audience.operation.modification;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AudienceOperationResultResponse {

    private static final String JSON_MEMBER_COUNT = "member_count";
    private static final String JSON_ANONYMOUS_COUNT = "anonymous_count";
    private static final String JSON_NON_PROCESSED_COUNT = "non_processed_count";
    private static final String JSON_LAST_UPDATE = "last_update";

    private final long memberCount;
    private final long anonymousCount;
    private final long nonProcessedCount;
    private final ZonedDateTime lastUpdateDate;

    public AudienceOperationResultResponse(@JsonProperty(JSON_MEMBER_COUNT) long memberCount,
        @JsonProperty(JSON_ANONYMOUS_COUNT) long anonymousCount,
        @JsonProperty(JSON_NON_PROCESSED_COUNT) long nonProcessedCount,
        @JsonProperty(JSON_LAST_UPDATE) ZonedDateTime lastUpdateDate) {
        this.memberCount = memberCount;
        this.anonymousCount = anonymousCount;
        this.nonProcessedCount = nonProcessedCount;
        this.lastUpdateDate = lastUpdateDate;
    }

    @JsonProperty(JSON_MEMBER_COUNT)
    public long getMemberCount() {
        return memberCount;
    }

    @JsonProperty(JSON_ANONYMOUS_COUNT)
    public long getAnonymousCount() {
        return anonymousCount;
    }

    @JsonProperty(JSON_NON_PROCESSED_COUNT)
    public long getNonProcessedCount() {
        return nonProcessedCount;
    }

    @JsonProperty(JSON_LAST_UPDATE)
    public ZonedDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
