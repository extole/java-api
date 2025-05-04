package com.extole.client.rest.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientSettingsResponse {

    private static final String TIME_ZONE = "time_zone";
    private static final String HAS_PRIOR_STEP_RESPECT_CONTAINER = "has_prior_step_respect_container";
    private static final String ISOLATE_OLD_DEVICES = "isolate_old_devices";

    private final String timeZone;
    private final Boolean hasPriorStepRespectContainer;
    private final Boolean isolateOldDevices;

    @JsonCreator
    public ClientSettingsResponse(
        @JsonProperty(TIME_ZONE) String timeZone,
        @JsonProperty(HAS_PRIOR_STEP_RESPECT_CONTAINER) Boolean hasPriorStepRespectContainer,
        @JsonProperty(ISOLATE_OLD_DEVICES) Boolean isolateOldDevices) {
        this.timeZone = timeZone;
        this.hasPriorStepRespectContainer = hasPriorStepRespectContainer;
        this.isolateOldDevices = isolateOldDevices;
    }

    @JsonProperty(TIME_ZONE)
    public String getTimeZone() {
        return timeZone;
    }

    @JsonProperty(HAS_PRIOR_STEP_RESPECT_CONTAINER)
    public Boolean getHasPriorStepRespectContainer() {
        return hasPriorStepRespectContainer;
    }

    @JsonProperty(ISOLATE_OLD_DEVICES)
    public Boolean getIsolateOldDevices() {
        return isolateOldDevices;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
