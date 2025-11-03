package com.extole.common.memcached.lock;

import com.extole.common.lang.ToString;

public class CompositeLock extends Lock {
    private final Lock oldLock;
    private final Lock newLock;

    CompositeLock(Lock oldLock, Lock newLock) {
        super(newLock);
        this.oldLock = oldLock;
        this.newLock = newLock;
    }

    public Lock getOldLock() {
        return oldLock;
    }

    public Lock getNewLock() {
        return newLock;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
