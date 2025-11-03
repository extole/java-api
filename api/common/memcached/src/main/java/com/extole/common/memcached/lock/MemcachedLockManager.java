package com.extole.common.memcached.lock;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import com.extole.common.lock.LockAcquireException;
import com.extole.common.lock.LockDescription;
import com.extole.common.lock.LockKey;
import com.extole.common.lock.LockServiceException;

interface MemcachedLockManager {
    Lock lock(LockKey key, LockDescription description, Duration maxLockDuration, Duration maxAcquireDuration)
        throws LockAcquireException, InterruptedException;

    Optional<Lock> lockForAtomicOperation(LockKey key, LockDescription description, Duration maxLockDuration,
        Duration sincePreviousLockDuration) throws LockServiceException, InterruptedException;

    void releaseLock(Lock lock, Supplier<Instant> expirationTimeSupplier) throws InterruptedException;

    default void releaseLock(Lock lock) throws InterruptedException {
        releaseLock(lock, () -> lock.getCreationTime());
    }
}
