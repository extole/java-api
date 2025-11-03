package com.extole.common.memcached;

import java.time.Duration;

public class MemcachedExpiration {
    private static final int MIN_EXPIRATION_SECONDS = 1;
    private static final int MAX_EXPIRATION_DAYS = 30;

    private final int seconds;

    public MemcachedExpiration(Duration expireIn) throws InvalidMemcachedExpirationException {
        if (expireIn.getSeconds() < MIN_EXPIRATION_SECONDS) {
            throw new InvalidMemcachedExpirationException("Expiration duration should be greater than 0 seconds");
        }
        if (expireIn.toDays() > MAX_EXPIRATION_DAYS) {
            throw new InvalidMemcachedExpirationException("Expiration duration should be less than 30 days");
        }
        this.seconds = (int) expireIn.getSeconds();
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(seconds=" + seconds + ")";
    }
}
