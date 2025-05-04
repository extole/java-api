package com.extole.reporting.rest.fixup.filter;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

public class ProfileIdsFixupFilterRequest {
    private static final String JSON_PROFILE_IDS = "profile_ids";

    private final Set<String> profileIds;

    @JsonCreator
    public ProfileIdsFixupFilterRequest(
        @Nullable @JsonProperty(JSON_PROFILE_IDS) Set<String> profileIds) {
        this.profileIds = profileIds != null ? ImmutableSet.copyOf(profileIds) : null;
    }

    @Nullable
    @JsonProperty(JSON_PROFILE_IDS)
    public Set<String> getProfileIds() {
        return profileIds;
    }
}
