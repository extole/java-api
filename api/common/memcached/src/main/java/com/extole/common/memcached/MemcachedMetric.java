package com.extole.common.memcached;

public enum MemcachedMetric {
    ADD_DURATION("add.duration.ms"),
    ADD_MISS_DURATION("add.miss.ms"),
    ADD_FAIL_DURATION("add.fail.ms"),
    ADD_ATTEMPTS("add.attempts"),
    ADD_TIMEOUT_COUNTER("add.timeout"),

    SET_DURATION("write.duration.ms"),
    SET_MISS_DURATION("write.miss.ms"),
    SET_FAIL_DURATION("write.fail.ms"),
    SET_ATTEMPTS("write.attempts"),
    SET_TIMEOUT_COUNTER("write.timeout"),

    READ_DURATION("read.duration.ms"),
    READ_MISS_DURATION("read.miss.ms"),
    READ_FAIL_DURATION("read.fail.ms"),
    READ_ATTEMPTS("read.attempts"),
    READ_TIMEOUT_COUNTER("read.timeout"),

    INCREMENT_DURATION("increment.duration.ms"),
    INCREMENT_FAIL_DURATION("increment.fail.ms"),

    DECREMENT_DURATION("decrement.duration.ms"),
    DECREMENT_FAIL_DURATION("decrement.fail.ms"),

    GETS_DURATION("gets.duration.ms"),
    GETS_MISS_DURATION("gets.miss.ms"),
    GETS_FAIL_DURATION("gets.fail.ms"),
    GETS_ATTEMPTS("gets.attempts"),
    GETS_TIMEOUT_COUNTER("gets.timeout"),

    BULK_GET_DURATION("bulk_get.duration.ms"),
    BULK_GET_MISS_DURATION("bulk_get.miss.ms"),
    BULK_GET_FAIL_DURATION("bulk_get.fail.ms"),
    BULK_GET_ATTEMPTS("bulk_get.attempts"),
    BULK_GET_TIMEOUT_COUNTER("bulk_get.timeout"),

    TOUCH_DURATION("touch.duration.ms"),
    TOUCH_MISS_DURATION("touch.miss.ms"),
    TOUCH_FAIL_DURATION("touch.fail.ms"),
    TOUCH_ATTEMPTS("touch.attempts"),
    TOUCH_TIMEOUT_COUNTER("touch.timeout"),

    CAS_DURATION("cas.duration.ms"),
    CAS_MISS_DURATION("cas.miss.ms"),
    CAS_FAIL_DURATION("cas.fail.ms"),
    CAS_ATTEMPTS("cas.attempts"),
    CAS_TIMEOUT_COUNTER("cas.timeout"),

    DELETE_DURATION("delete.duration.ms"),
    DELETE_MISS_DURATION("delete.miss.ms"),
    DELETE_FAIL_DURATION("delete.fail.ms"),
    DELETE_ATTEMPTS("delete.attempts"),
    DELETE_TIMEOUT_COUNTER("delete.timeout"),

    GET_AND_SET_DURATION("get_and_set.duration.ms"),
    GET_AND_SET_MISS_DURATION("get_and_set.miss.ms"),
    GET_AND_SET_FAIL_DURATION("get_and_set.fail.ms"),
    GET_AND_SET_ATTEMPTS("get_and_set.attempts"),
    GET_AND_SET_TIMEOUT_COUNTER("get_and_set.timeout"),
    GET_AND_SET_NOOP_DURATION("get_and_set.noop.ms"),

    SERIALIZE_TIME("serialize.duration.ms"),
    SERIALIZE_ERROR_COUNTER("serialize.error"),
    SERIALIZE_SIZE("serialize.size"),
    DESERIALIZE_TIME("deserialize.duration.ms"),
    DESERIALIZE_ERROR_COUNTER("deserialize.error"),

    COMPRESS_TIME("compress.duration.ms"),
    COMPRESS_SIZE("compress.size"),
    COMPRESS_ERROR_COUNTER("compress.error"),
    DECOMPRESS_TIME("decompress.duration.ms"),
    DECOMPRESS_ERROR_COUNTER("decompress.error");

    private final String metricName;

    MemcachedMetric(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        return "memcached." + metricName;
    }
}
