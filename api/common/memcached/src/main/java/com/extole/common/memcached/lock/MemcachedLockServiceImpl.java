package com.extole.common.memcached.lock;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.lock.LockAcquireException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.lock.LockKey;
import com.extole.common.lock.LockService;
import com.extole.common.lock.LockServiceException;

@Component
class MemcachedLockServiceImpl implements LockService {
    private final MemcachedLockManager memcachedLockManager;

    @Autowired
    MemcachedLockServiceImpl(MemcachedLockManager memcachedLockManager) {
        this.memcachedLockManager = memcachedLockManager;
    }

    @Override
    public <T> T executeWithinLock(LockKey key, LockDescription description, LockClosure<T> closure,
        Duration maxClosureDuration, Duration maxAcquireDuration)
        throws LockAcquireException, LockClosureException, InterruptedException {
        Lock lock = memcachedLockManager.lock(key, description, maxClosureDuration, maxAcquireDuration);
        try {
            return closure.execute();
        } finally {
            memcachedLockManager.releaseLock(lock);
        }
    }

    @Override
    public <T> Optional<T> atomicOperation(LockKey key, LockDescription description, LockClosure<T> closure,
        Duration maxClosureDuration, Duration sinceLastRun)
        throws LockServiceException, LockClosureException, InterruptedException {

        Lock lock = memcachedLockManager.lockForAtomicOperation(key, description, maxClosureDuration, sinceLastRun)
            .orElse(null);
        if (lock == null) {
            return Optional.empty();
        }

        Exception exception = null;
        try {
            return Optional.ofNullable(closure.execute());
        } catch (LockClosureException | RuntimeException e) {
            exception = e;
            throw e;
        } finally {
            if (exception != null) {
                memcachedLockManager.releaseLock(lock);
            } else {
                memcachedLockManager.releaseLock(lock, () -> Instant.MAX);
            }
        }
    }
}
