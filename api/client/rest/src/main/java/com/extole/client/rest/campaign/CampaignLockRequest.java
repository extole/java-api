package com.extole.client.rest.campaign;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CampaignLockRequest {

    private static final String LOCKS_TYPES = "lock_types";

    private final Set<CampaignLockType> lockTypes;

    @JsonCreator
    public CampaignLockRequest(@JsonProperty(LOCKS_TYPES) Set<CampaignLockType> lockTypes) {
        this.lockTypes = lockTypes == null ? Collections.emptySet() : lockTypes;
    }

    @JsonProperty(LOCKS_TYPES)
    public Set<CampaignLockType> getLockTypes() {
        return lockTypes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
