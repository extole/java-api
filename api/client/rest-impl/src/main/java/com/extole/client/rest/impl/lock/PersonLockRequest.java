package com.extole.client.rest.impl.lock;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonLockRequest {
    private static final String LOCKING_ID = "LOCKING_ID";

    private final String lockingId;

    public PersonLockRequest(@JsonProperty(LOCKING_ID) String lockingId) {
        this.lockingId = lockingId;
    }

    @JsonProperty(LOCKING_ID)
    public String getLockingId() {
        return lockingId;
    }
}
