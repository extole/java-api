package com.extole.common.lock;

public enum LockMetric {
    LOCK_ACQUIRE_DURATION("acquire.duration.ms"),
    LOCK_ACQUIRE_FAILURE("acquire.failure.ms"),
    LOCK_ACQUIRE_CANCELED("acquire.canceled.ms"),
    LOCK_ACQUIRE_ATTEMPTS("acquire.attempts"),
    LOCK_STOLEN("stolen.ms"),
    LOCK_CLOSURE_TIME("closure.duration.ms"),
    LOCK_HOLDING_DURATION("holding.duration.ms"),
    LOCK_RELEASE_NOT_FOUND("release.not.found.ms"),
    LOCK_RELEASE_NOT_OWNER("release.not.owner.ms");

    private final String metricName;

    LockMetric(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        return "lock." + metricName;
    }
}
