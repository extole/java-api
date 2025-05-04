package com.extole.client.rest.campaign;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CampaignPublishRequest {

    public static final CampaignPublishRequest LAUNCH_REQUEST =
        CampaignPublishRequest.builder()
            .withLaunch(true)
            .build();

    private static final String MESSAGE = "message";
    private static final String LAUNCH = "launch";

    private final String message;
    private final Boolean launch;

    @JsonCreator
    public CampaignPublishRequest(@Nullable @JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(LAUNCH) Boolean launch) {
        this.message = message;
        this.launch = launch;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return this.message;
    }

    @Nullable
    @JsonProperty(LAUNCH)
    public Boolean getLaunch() {
        return launch;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String message;
        private boolean launch = false;

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withLaunch(boolean launch) {
            this.launch = launch;
            return this;
        }

        public CampaignPublishRequest build() {
            return new CampaignPublishRequest(message, Boolean.valueOf(launch));
        }

        private Builder() {

        }
    }

}
