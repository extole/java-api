package com.extole.common.memcached.lock;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.lock.LockAcquireException;
import com.extole.common.lock.LockDescription;
import com.extole.common.lock.LockKey;
import com.extole.common.lock.LockMetric;
import com.extole.common.lock.LockServiceException;
import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedException;
import com.extole.common.memcached.MalformedMemcachedKeyException;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.Id;

class MemcachedLockManagerImpl implements MemcachedLockManager {
    private static final Logger LOG = LoggerFactory.getLogger(MemcachedLockManagerImpl.class);

    private final String instanceName;
    private final int maxLockAttempts;
    private final int pollingMinSleepTimeMs;
    private final ExtoleMetricRegistry extoleMetricRegistry;
    private final ExtoleMemcachedClient<String, LockPojo> memcachedClient;

    MemcachedLockManagerImpl(String instanceName, int pollingMinSleepTimeMs, int maxLockAttempts,
        ExtoleMetricRegistry extoleMetricRegistry,
        ExtoleMemcachedClient<String, LockPojo> memcachedClient) {
        this.instanceName = instanceName;
        this.pollingMinSleepTimeMs = pollingMinSleepTimeMs;
        this.maxLockAttempts = maxLockAttempts;
        this.extoleMetricRegistry = extoleMetricRegistry;
        this.memcachedClient = memcachedClient;
    }

    @Override
    public Lock lock(LockKey key, LockDescription description, Duration maxLockDuration,
        Duration maxAcquireDuration) throws LockAcquireException, InterruptedException {

        LockPojo initialLock = createInitialLockPojo(key, description, maxLockDuration);
        Instant attemptEndTime = initialLock.getCreationTime().plusMillis(maxAcquireDuration.toMillis());
        double pollIncrementMs = maxAcquireDuration.toMillis() / maxLockAttempts;

        ExtoleMemcachedException lastException = null;
        int lockAttempt = 0;
        boolean isLockAcquired = false;
        boolean isReLock = false;
        double pollingSleepTimeMs = pollingMinSleepTimeMs;
        Optional<LockPojo> lock = Optional.empty();
        try {
            while (!isLockAcquired
                && (lockAttempt < 1 || Instant.now().isBefore(attemptEndTime))
                && !Thread.currentThread().isInterrupted()) {

                if (lockAttempt > 0) {
                    LOG.debug("Attempt {} to lock {} failed, it is owned by {}. Sleeping for {}ms.", lockAttempt,
                        initialLock.getKey(), lock.orElse(null), pollingSleepTimeMs);
                    Thread.sleep(Double.valueOf(pollingSleepTimeMs).longValue());
                    pollingSleepTimeMs = pollingSleepTimeMs + pollIncrementMs;
                }

                lockAttempt++;
                try {
                    lock = acquireLock(initialLock, Optional.empty());
                    if (lock.isPresent()) {
                        if (initialLock.getId().equals(lock.get().getId())) {
                            isLockAcquired = true;
                        } else if (lock.get().getHost().equals(instanceName)
                            && lock.get().getThreadId().equals(initialLock.getThreadId())) {
                            isLockAcquired = true;
                            isReLock = true;
                        }
                    }
                } catch (MalformedMemcachedKeyException e) {
                    LOG.warn("Attempted to read malformed personLock key: {} lock: {}", initialLock.getKey(),
                        initialLock, e);
                    lastException = e;
                    break;
                } catch (ExtoleMemcachedException e) {
                    lastException = e;
                }
            }
        } finally {
            updateHistogram(initialLock.getDescription(), LockMetric.LOCK_ACQUIRE_ATTEMPTS, lockAttempt);
        }

        if (lock.isPresent() && isLockAcquired) {
            updateHistogram(initialLock.getDescription(), LockMetric.LOCK_ACQUIRE_DURATION,
                initialLock.getCreationTime());
            LOG.debug("Obtained lock {} for {} from attempt {}", lock, initialLock.getKey(), lockAttempt);
            LockPojo resultLock = isReLock ? LockPojo.builder()
                .withId(lock.get().getId())
                .withKey(lock.get().getKey())
                .withHost(lock.get().getHost())
                .withThreadId(lock.get().getThreadId())
                .withDescription(description.getDescription())
                .withCreationTime(lock.get().getCreationTime())
                .withExpirationTime(lock.get().getExpirationTime())
                .build() : lock.get();
            return new Lock(resultLock, isReLock);
        }

        updateHistogram(initialLock.getDescription(), LockMetric.LOCK_ACQUIRE_FAILURE, initialLock.getCreationTime());

        StringBuilder exceptionMessageBuilder = new StringBuilder();
        exceptionMessageBuilder.append(String.format(
            "Unable to acquire lock %s for key %s hash %s after %s attempts in %s ms.",
            initialLock.getDescription(), initialLock.getKey(), hashKey(initialLock.getKey()),
            Integer.valueOf(lockAttempt),
            Long.valueOf(Instant.now().toEpochMilli() - initialLock.getCreationTime().toEpochMilli())));

        if (Thread.currentThread().isInterrupted()) {
            exceptionMessageBuilder.append(" The thread was interrupted.");
        }

        if (lastException == null) {
            throw new LockAcquireException(exceptionMessageBuilder.toString());
        }
        throw new LockAcquireException(exceptionMessageBuilder.toString(), lastException);
    }

    @Override
    public Optional<Lock> lockForAtomicOperation(LockKey key, LockDescription description, Duration maxLockDuration,
        Duration sincePreviousLockDuration) throws LockServiceException, InterruptedException {
        Instant startTime = Instant.now();
        LockPojo initialLock = createInitialLockPojo(key, description, maxLockDuration);
        try {
            LockPojo lock = acquireLock(initialLock, Optional.of(sincePreviousLockDuration)).orElse(null);
            if (lock != null && lock.getId().equals(initialLock.getId())) {
                updateHistogram(lock.getDescription(), LockMetric.LOCK_ACQUIRE_DURATION, lock.getCreationTime());
                return Optional.of(new Lock(lock, false));
            }
            LOG.info("{} initial lock {} existing lock {}, cancelling operation", key, initialLock, lock);
            updateHistogram(description.getDescription(), LockMetric.LOCK_ACQUIRE_CANCELED, startTime);
            return Optional.empty();
        } catch (ExtoleMemcachedException e) {
            updateHistogram(description.getDescription(), LockMetric.LOCK_ACQUIRE_FAILURE, startTime);
            throw new LockServiceException("Failed to execute atomic operation with key: " + key, e);
        }
    }

    private Optional<LockPojo> acquireLock(LockPojo initialLock, Optional<Duration> sincePreviousLockDuration)
        throws ExtoleMemcachedException, InterruptedException {
        String hashedKey = hashKey(initialLock.getKey());
        Instant now = Instant.now();
        return memcachedClient.getAndOptionallySet(hashedKey, existingLockPojo -> {
            if (existingLockPojo.isPresent() && !isReleased(existingLockPojo.get())) {
                if (isExpired(existingLockPojo.get(), now)) {
                    updateHistogram(existingLockPojo.get().getDescription(), LockMetric.LOCK_STOLEN,
                        existingLockPojo.get().getCreationTime());
                    LOG.warn("lock {} has expired, acquiring for new purpose {}",
                        existingLockPojo.get(), initialLock.getDescription());
                } else if (!sincePreviousLockDuration.isPresent() ||
                    existingLockPojo.get().getCreationTime().plus(sincePreviousLockDuration.get()).isAfter(now)) {
                    return Optional.empty();
                }
            }
            return Optional.of(initialLock);
        });
    }

    @Override
    public void releaseLock(Lock lock, Supplier<Instant> expirationTimeSupplier)
        throws InterruptedException {
        if (lock.isReLock()) {
            return;
        }
        String hashedKey = hashKey(lock.getKey());
        try {
            memcachedClient.getAndOptionallySet(hashedKey, existingLockPojo -> {
                if (existingLockPojo.isPresent()) {
                    if (existingLockPojo.get().getId().getValue().equals(lock.getId().getValue())) {
                        updateHistogram(lock.getDescription(), LockMetric.LOCK_HOLDING_DURATION,
                            lock.getCreationTime());
                        return Optional.of(newBuilder(lock).withExpirationTime(expirationTimeSupplier.get()).build());
                    }
                    updateHistogram(lock.getDescription(), LockMetric.LOCK_RELEASE_NOT_OWNER, lock.getCreationTime());
                } else {
                    updateHistogram(lock.getDescription(), LockMetric.LOCK_RELEASE_NOT_FOUND, lock.getCreationTime());
                }
                LOG.error("Failed to release lock {} - lock stolen by - {}", lock, existingLockPojo);
                return Optional.empty();
            });
        } catch (ExtoleMemcachedException e) {
            LOG.error("Memcached failure prevented release of lock {}", lock, e);
        }
    }

    private LockPojo createInitialLockPojo(LockKey key, LockDescription description, Duration maxClosureDuration) {
        String currentThreadId = String.valueOf(Thread.currentThread().getId());
        Instant now = Instant.now();
        return LockPojo.builder()
            .withKey(key.getValue())
            .withHost(instanceName)
            .withThreadId(currentThreadId)
            .withDescription(description.getDescription())
            .withCreationTime(now)
            .withExpirationTime(now.plusMillis(maxClosureDuration.toMillis()))
            .build();
    }

    private void updateHistogram(String description, LockMetric metric, Instant startTime) {
        extoleMetricRegistry.histogram(buildMetricName(description, metric)).update(startTime, Instant.now());
    }

    private void updateHistogram(String description, LockMetric metric, long value) {
        extoleMetricRegistry.histogram(buildMetricName(description, metric)).update(value);
    }

    private static String buildMetricName(String description, LockMetric metric) {
        return "lock." + description + "." + metric.getMetricName();
    }

    private static LockPojo.Builder newBuilder(Lock lock) {
        return LockPojo.builder()
            .withId(Id.valueOf(lock.getId().getValue()))
            .withKey(lock.getKey())
            .withHost(lock.getHost())
            .withThreadId(lock.getThreadId())
            .withDescription(lock.getDescription())
            .withCreationTime(lock.getCreationTime())
            .withExpirationTime(lock.getExpirationTime());
    }

    private static String hashKey(String key) {
        return DigestUtils.md5Hex(key);
    }

    private static boolean isExpired(LockPojo lock, Instant now) {
        return lock.getExpirationTime().isBefore(now);
    }

    private static boolean isReleased(LockPojo lock) {
        return lock.getExpirationTime().equals(lock.getCreationTime());
    }
}
