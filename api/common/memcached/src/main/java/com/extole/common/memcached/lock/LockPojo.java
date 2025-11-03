package com.extole.common.memcached.lock;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.id.IdGenerator;

class LockPojo {
    private static final IdGenerator ID_GENERATOR = new IdGenerator();

    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String HOST = "host";
    private static final String THREAD_ID = "thread_id";
    private static final String DESCRIPTION = "description";
    private static final String CREATION_TIME = "creation_time";
    private static final String EXPIRATION_TIME = "expiration_time";

    private final Id<LockPojo> id;
    private final String key;
    private final String host;
    private final String threadId;
    private final String description;
    private final Instant creationTime;
    private final Instant expirationTime;

    LockPojo(@JsonProperty(ID) Id<LockPojo> id,
        @JsonProperty(KEY) String key,
        @JsonProperty(HOST) String host,
        @JsonProperty(THREAD_ID) String threadId,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(CREATION_TIME) Instant creationTime,
        @JsonProperty(EXPIRATION_TIME) Instant expirationTime) {
        this.id = id;
        this.key = key;
        this.host = host;
        this.threadId = threadId;
        this.description = description;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    @JsonProperty(ID)
    Id<LockPojo> getId() {
        return id;
    }

    @JsonProperty(KEY)
    String getKey() {
        return key;
    }

    @JsonProperty(HOST)
    String getHost() {
        return host;
    }

    @JsonProperty(THREAD_ID)
    String getThreadId() {
        return threadId;
    }

    @JsonProperty(DESCRIPTION)
    String getDescription() {
        return description;
    }

    @JsonProperty(CREATION_TIME)
    Instant getCreationTime() {
        return creationTime;
    }

    @JsonProperty(EXPIRATION_TIME)
    Instant getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    static final class Builder {
        private Id<LockPojo> id;
        private String key;
        private String host;
        private String threadId;
        private String description;
        private Instant creationTime;
        private Instant expirationTime;

        private Builder() {
            this.id = ID_GENERATOR.generateId();
        }

        Builder withId(Id<LockPojo> id) {
            this.id = id;
            return this;
        }

        Builder withKey(String key) {
            this.key = key;
            return this;
        }

        Builder withHost(String host) {
            this.host = host;
            return this;
        }

        Builder withThreadId(String threadId) {
            this.threadId = threadId;
            return this;
        }

        Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        Builder withCreationTime(Instant creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        Builder withExpirationTime(Instant expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        LockPojo build() {
            return new LockPojo(id, key, host, threadId, description, creationTime, expirationTime);
        }
    }
}
