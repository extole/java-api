package com.extole.reporting.rest.audience.stats;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class AudienceStatsResponse {
    private static final String ACTIVE_MEMBERS_COUNT = "active_members_count";
    private final long activeMembersCount;

    @JsonCreator
    public AudienceStatsResponse(
        @JsonProperty(ACTIVE_MEMBERS_COUNT) long activeMembersCount) {
        this.activeMembersCount = activeMembersCount;
    }

    @JsonProperty(ACTIVE_MEMBERS_COUNT)
    public long getActiveMembersCount() {
        return activeMembersCount;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
