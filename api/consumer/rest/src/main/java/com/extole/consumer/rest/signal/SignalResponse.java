package com.extole.consumer.rest.signal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public class SignalResponse {

    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_SIGNALS = "signals";

    private final String pollingId;
    private final List<Signal> signals;

    @JsonCreator
    public SignalResponse(@JsonProperty(JSON_POLLING_ID) String pollingId,
        @JsonProperty(JSON_SIGNALS) List<Signal> signals) {
        this.pollingId = pollingId;
        this.signals = ImmutableList.copyOf(signals);
    }

    @JsonProperty(JSON_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty(JSON_SIGNALS)
    public List<Signal> getSignals() {
        return signals;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
