package com.extole.client.rest.campaign;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class CampaignLaunchBurstRequest {

    private static final String MESSAGE = "message";
    private static final String START_DATE = "start_date";
    private static final String STOP_DATE = "stop_date";

    private final Omissible<Optional<String>> message;
    private final Omissible<Optional<ZonedDateTime>> startDate;
    private final Omissible<Optional<ZonedDateTime>> stopDate;

    CampaignLaunchBurstRequest(@JsonProperty(MESSAGE) Omissible<Optional<String>> message,
        @JsonProperty(START_DATE) Omissible<Optional<ZonedDateTime>> startDate,
        @JsonProperty(STOP_DATE) Omissible<Optional<ZonedDateTime>> stopDate) {
        this.message = message;
        this.startDate = startDate;
        this.stopDate = stopDate;
    }

    @JsonProperty(MESSAGE)
    public Omissible<Optional<String>> getMessage() {
        return message;
    }

    @JsonProperty(START_DATE)
    public Omissible<Optional<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    @JsonProperty(STOP_DATE)
    public Omissible<Optional<ZonedDateTime>> getStopDate() {
        return stopDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> message = Omissible.omitted();
        private Omissible<Optional<ZonedDateTime>> startDate = Omissible.omitted();
        private Omissible<Optional<ZonedDateTime>> stopDate = Omissible.omitted();

        private Builder() {
        }

        public Builder withMessage(String message) {
            this.message = Omissible.of(Optional.ofNullable(message));
            return this;
        }

        public Builder withStartDate(ZonedDateTime startDate) {
            this.startDate = Omissible.of(Optional.ofNullable(startDate));
            return this;
        }

        public Builder withStopDate(ZonedDateTime stopDate) {
            this.stopDate = Omissible.of(Optional.ofNullable(stopDate));
            return this;
        }

        public CampaignLaunchBurstRequest build() {
            return new CampaignLaunchBurstRequest(message, startDate, stopDate);
        }
    }
}
