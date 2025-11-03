package com.extole.common.lock;

import java.time.Duration;
import java.util.Optional;

public interface LockService {

    <T> T executeWithinLock(LockKey key, LockDescription description, LockClosure<T> closure,
        Duration maxClosureDuration, Duration maxAcquireDuration)
        throws LockAcquireException, LockClosureException, InterruptedException;

    <T> Optional<T> atomicOperation(LockKey key, LockDescription description, LockClosure<T> closure,
        Duration maxClosureDuration, Duration sinceLastRun)
        throws LockServiceException, LockClosureException, InterruptedException;

    @FunctionalInterface
    interface LockClosure<T> {
        T execute() throws LockClosureException;
    }
}
