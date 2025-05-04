package com.extole.client.rest.impl.lock;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonUpdateRequest {
    private static final String LOCK_DURATION = "lock_duration_ms";
    private static final String DATA = "data";

    private final long lockDurationMs;
    private final Map<String, String> data;

    public PersonUpdateRequest(@JsonProperty(LOCK_DURATION) long lockDurationMs,
        @JsonProperty(DATA) Map<String, String> data) {
        this.lockDurationMs = lockDurationMs;
        this.data = data;
    }

    @JsonProperty(LOCK_DURATION)
    public long getLockDurationMs() {
        return lockDurationMs;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }
}
