package com.extole.common.memcached.lock;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.lock.LockAcquireException;
import com.extole.common.lock.LockDescription;
import com.extole.common.lock.LockKey;
import com.extole.common.lock.LockServiceException;

public class CompositeMemcachedLockManager implements MemcachedLockManager {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeMemcachedLockManager.class);

    private final MemcachedLockManager oldMemcachedLockManager;
    private final MemcachedLockManager newMemcachedLockManager;

    public CompositeMemcachedLockManager(MemcachedLockManager oldMemcachedLockManager,
        MemcachedLockManager newMemcachedLockManager) {
        this.oldMemcachedLockManager = oldMemcachedLockManager;
        this.newMemcachedLockManager = newMemcachedLockManager;
    }

    @Override
    public Lock lock(LockKey key, LockDescription description, Duration maxLockDuration, Duration maxAcquireDuration)
        throws LockAcquireException, InterruptedException {
        Lock newLock = newMemcachedLockManager.lock(key, description, maxLockDuration, maxAcquireDuration);
        try {
            Lock oldLock = oldMemcachedLockManager.lock(key, description, maxLockDuration, maxAcquireDuration);
            return new CompositeLock(oldLock, newLock);
        } catch (LockAcquireException | InterruptedException e) {
            newMemcachedLockManager.releaseLock(newLock);
            throw e;
        }
    }

    @Override
    public Optional<Lock> lockForAtomicOperation(LockKey key, LockDescription description, Duration maxLockDuration,
        Duration sincePreviousLockDuration) throws LockServiceException, InterruptedException {
        Lock newLock = newMemcachedLockManager.lockForAtomicOperation(key, description, maxLockDuration,
            sincePreviousLockDuration).orElse(null);
        if (newLock == null) {
            return Optional.empty();
        }
        try {
            Lock oldLock = oldMemcachedLockManager.lockForAtomicOperation(key, description, maxLockDuration,
                sincePreviousLockDuration).orElse(null);
            if (oldLock == null) {
                newMemcachedLockManager.releaseLock(newLock);
                return Optional.empty();
            }
            return Optional.of(new CompositeLock(oldLock, newLock));
        } catch (LockServiceException | InterruptedException e) {
            newMemcachedLockManager.releaseLock(newLock);
            throw e;
        }
    }

    @Override
    public void releaseLock(Lock lock, Supplier<Instant> expirationTimeSupplier) throws InterruptedException {
        if (lock instanceof CompositeLock) {
            CompositeLock compositeLock = (CompositeLock) lock;
            InterruptedException exception = null;
            try {
                newMemcachedLockManager.releaseLock(compositeLock.getNewLock(), expirationTimeSupplier);
            } catch (InterruptedException e) {
                exception = e;
            }
            try {
                oldMemcachedLockManager.releaseLock(compositeLock.getOldLock(), expirationTimeSupplier);
            } catch (InterruptedException e) {
                if (exception != null) {
                    e.addSuppressed(exception);
                }
                exception = e;
            }
            if (exception != null) {
                throw exception;
            }
        } else {
            LOG.warn("Releasing non-composite lock: {}", lock);
            newMemcachedLockManager.releaseLock(lock, expirationTimeSupplier);
        }
    }
}
