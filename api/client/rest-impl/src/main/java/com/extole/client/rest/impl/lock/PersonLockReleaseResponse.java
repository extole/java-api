package com.extole.client.rest.impl.lock;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonLockReleaseResponse {
    private static final String LOCK_RELEASED = "lock_released";

    private final boolean isLockReleased;

    public PersonLockReleaseResponse(@JsonProperty(LOCK_RELEASED) boolean isLockReleased) {
        this.isLockReleased = isLockReleased;
    }

    @JsonProperty(LOCK_RELEASED)
    public boolean isLockReleased() {
        return isLockReleased;
    }
}
