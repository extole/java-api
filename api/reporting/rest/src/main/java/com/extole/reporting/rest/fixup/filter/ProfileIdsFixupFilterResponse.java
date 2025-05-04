package com.extole.reporting.rest.fixup.filter;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

public class ProfileIdsFixupFilterResponse extends FixupFilterResponse {
    private static final String JSON_PROFILE_IDS = "profile_ids";

    private final Set<String> profileIds;

    @JsonCreator
    public ProfileIdsFixupFilterResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupFilterType type,
        @JsonProperty(JSON_PROFILE_IDS) Set<String> profileIds) {
        super(id, type);
        this.profileIds = profileIds != null ? ImmutableSet.copyOf(profileIds) : Collections.emptySet();
    }

    @JsonProperty(JSON_PROFILE_IDS)
    public Set<String> getProfileIds() {
        return profileIds;
    }
}
