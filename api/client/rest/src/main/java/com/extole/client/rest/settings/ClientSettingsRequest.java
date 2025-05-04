package com.extole.client.rest.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ClientSettingsRequest {

    private static final String TIME_ZONE = "time_zone";
    private static final String HAS_PRIOR_STEP_RESPECT_CONTAINER = "has_prior_step_respect_container";
    private static final String ISOLATE_OLD_DEVICES = "isolate_old_devices";

    private final Omissible<String> timeZone;
    private final Omissible<Boolean> hasPriorStepRespectContainer;
    private final Omissible<Boolean> isolateOldDevices;

    public ClientSettingsRequest(
        @JsonProperty(TIME_ZONE) Omissible<String> timeZone,
        @JsonProperty(HAS_PRIOR_STEP_RESPECT_CONTAINER) Omissible<Boolean> hasPriorStepRespectContainer,
        @JsonProperty(ISOLATE_OLD_DEVICES) Omissible<Boolean> isolateOldDevices) {
        this.timeZone = timeZone;
        this.hasPriorStepRespectContainer = hasPriorStepRespectContainer;
        this.isolateOldDevices = isolateOldDevices;
    }

    @JsonProperty(TIME_ZONE)
    public Omissible<String> getTimeZone() {
        return timeZone;
    }

    @JsonProperty(HAS_PRIOR_STEP_RESPECT_CONTAINER)
    public Omissible<Boolean> getHasPriorStepRespectContainer() {
        return hasPriorStepRespectContainer;
    }

    @JsonProperty(ISOLATE_OLD_DEVICES)
    public Omissible<Boolean> getIsolateOldDevices() {
        return isolateOldDevices;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<String> timeZone = Omissible.omitted();
        private Omissible<Boolean> hasPriorStepRespectContainer = Omissible.omitted();
        private Omissible<Boolean> isolateOldDevices = Omissible.omitted();

        public Builder withTimeZone(String timeZone) {
            this.timeZone = Omissible.of(timeZone);
            return this;
        }

        public Builder withHasPriorStepRespectContainer(Boolean hasPriorStepRespectContainer) {
            this.hasPriorStepRespectContainer = Omissible.of(hasPriorStepRespectContainer);
            return this;
        }

        public Builder withIsolateOldDevices(Boolean isolateOldDevices) {
            this.isolateOldDevices = Omissible.of(isolateOldDevices);
            return this;
        }

        public ClientSettingsRequest build() {
            return new ClientSettingsRequest(timeZone, hasPriorStepRespectContainer, isolateOldDevices);
        }

    }

}
