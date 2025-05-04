package com.extole.client.rest.impl.lock;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonLockReleaseRequest {
    private static final String LOCK_ID = "LOCK_ID";
    private static final String LOCK_DESCRIPTION = "LOCK_DESCRIPTION";
    private static final String THREAD_ID = "THREAD_ID";
    private static final String HOST = "HOST";
    private static final String LOCK_EXPIRATION_TIME = "LOCK_EXPIRATION_TIME";
    private static final String LOCK_CREATION_TIME = "LOCK_CREATION_TIME";
    private static final String LOCKING_ID = "LOCKING_ID";

    private final String lockId;
    private final String lockDescription;
    private final long threadId;
    private final String host;
    private final long lockExpirationTime;
    private final long lockCreationTime;
    private final String lockingId;

    public PersonLockReleaseRequest(@JsonProperty(LOCK_ID) String lockId,
        @JsonProperty(LOCK_DESCRIPTION) String lockDescription,
        @JsonProperty(THREAD_ID) long threadId,
        @JsonProperty(HOST) String host,
        @JsonProperty(LOCK_EXPIRATION_TIME) long lockExpirationTime,
        @JsonProperty(LOCK_CREATION_TIME) long lockCreationTime,
        @JsonProperty(LOCKING_ID) String lockingId) {
        this.lockId = lockId;
        this.lockDescription = lockDescription;
        this.threadId = threadId;
        this.host = host;
        this.lockExpirationTime = lockExpirationTime;
        this.lockCreationTime = lockCreationTime;
        this.lockingId = lockingId;
    }

    @JsonProperty(LOCK_ID)
    public String getLockId() {
        return lockId;
    }

    @JsonProperty(LOCK_DESCRIPTION)
    public String getLockDescription() {
        return lockDescription;
    }

    @JsonProperty(THREAD_ID)
    public long getThreadId() {
        return threadId;
    }

    @JsonProperty(HOST)
    public String getHost() {
        return host;
    }

    @JsonProperty(LOCK_CREATION_TIME)
    public long getLockCreationTime() {
        return lockCreationTime;
    }

    @JsonProperty(LOCK_EXPIRATION_TIME)
    public long getLockExpirationTime() {
        return lockExpirationTime;
    }

    @JsonProperty(LOCKING_ID)
    public String getLockingId() {
        return lockingId;
    }
}
