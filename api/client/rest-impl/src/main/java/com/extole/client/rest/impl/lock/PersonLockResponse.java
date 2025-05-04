package com.extole.client.rest.impl.lock;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonLockResponse {
    public static final PersonLockResponse EMPTY_LOCK =
        new PersonLockResponse(null, null, null, null, null, null, false);

    private static final String LOCK_ID = "LOCK_ID";
    private static final String LOCK_DESCRIPTION = "LOCK_DESCRIPTION";
    private static final String THREAD_ID = "THREAD_ID";
    private static final String HOST = "HOST";
    private static final String LOCK_EXPIRATION_TIME = "LOCK_EXPIRATION_TIME";
    private static final String LOCK_CREATION_TIME = "LOCK_CREATION_TIME";
    private static final String IS_VIRTUAL = "IS_VIRTUAL";

    private final String lockId;
    private final String lockDescription;
    private final Long threadId;
    private final String host;
    private final Long lockExpirationTime;
    private final Long lockCreationTime;
    private final boolean isReLock;

    public PersonLockResponse(@JsonProperty(LOCK_ID) String lockId,
        @JsonProperty(LOCK_DESCRIPTION) String lockDescription,
        @JsonProperty(THREAD_ID) Long threadId,
        @JsonProperty(HOST) String host,
        @JsonProperty(LOCK_EXPIRATION_TIME) Long lockExpirationTime,
        @JsonProperty(LOCK_CREATION_TIME) Long lockCreationTime,
        @JsonProperty(IS_VIRTUAL) boolean isReLock) {
        this.lockId = lockId;
        this.lockDescription = lockDescription;
        this.threadId = threadId;
        this.host = host;
        this.lockExpirationTime = lockExpirationTime;
        this.lockCreationTime = lockCreationTime;
        this.isReLock = isReLock;
    }

    @JsonProperty(LOCK_ID)
    @Nullable
    public String getLockId() {
        return lockId;
    }

    @JsonProperty(LOCK_DESCRIPTION)
    @Nullable
    public String getLockDescription() {
        return lockDescription;
    }

    @JsonProperty(THREAD_ID)
    @Nullable
    public Long getThreadId() {
        return threadId;
    }

    @JsonProperty(HOST)
    @Nullable
    public String getHost() {
        return host;
    }

    @JsonProperty(LOCK_CREATION_TIME)
    @Nullable
    public Long getLockCreationTime() {
        return lockCreationTime;
    }

    @JsonProperty(LOCK_EXPIRATION_TIME)
    @Nullable
    public Long getLockExpirationTime() {
        return lockExpirationTime;
    }

    @JsonProperty(IS_VIRTUAL)
    public boolean isReLock() {
        return isReLock;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
