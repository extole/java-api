package com.extole.client.rest.impl.lock;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonLockStatusResponse {
    private static final String LOCK_OWNED = "lock_owned";

    private final boolean isLockOwned;

    public PersonLockStatusResponse(@JsonProperty(LOCK_OWNED) boolean isLockOwned) {
        this.isLockOwned = isLockOwned;
    }

    @JsonProperty(LOCK_OWNED)
    public boolean isLockOwned() {
        return isLockOwned;
    }
}
