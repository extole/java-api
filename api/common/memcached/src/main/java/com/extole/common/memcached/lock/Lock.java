package com.extole.common.memcached.lock;

import java.time.Instant;

import com.extole.common.lang.ToString;
import com.extole.id.Id;

class Lock {
    private final LockPojo lockPojo;
    private final boolean reLock;

    Lock(LockPojo lockPojo, boolean reLock) {
        this.lockPojo = lockPojo;
        this.reLock = reLock;
    }

    Lock(Lock lock) {
        this(lock.lockPojo, lock.reLock);
    }

    Id<Lock> getId() {
        return Id.valueOf(lockPojo.getId().getValue());
    }

    String getKey() {
        return lockPojo.getKey();
    }

    String getHost() {
        return lockPojo.getHost();
    }

    String getThreadId() {
        return lockPojo.getThreadId();
    }

    String getDescription() {
        return lockPojo.getDescription();
    }

    Instant getCreationTime() {
        return lockPojo.getCreationTime();
    }

    Instant getExpirationTime() {
        return lockPojo.getExpirationTime();
    }

    public boolean isReLock() {
        return reLock;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
