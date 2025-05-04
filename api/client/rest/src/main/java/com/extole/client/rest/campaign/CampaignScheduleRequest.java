package com.extole.client.rest.campaign;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class CampaignScheduleRequest {

    private static final String START_DATE = "start_date";
    private static final String STOP_DATE = "stop_date";
    private static final String PAUSE_DATE = "pause_date";
    private static final String END_DATE = "end_date";

    private final Omissible<Optional<ZonedDateTime>> startDate;
    private final Omissible<Optional<ZonedDateTime>> stopDate;
    private final Omissible<Optional<ZonedDateTime>> pauseDate;
    private final Omissible<Optional<ZonedDateTime>> endDate;

    @JsonCreator
    CampaignScheduleRequest(@JsonProperty(START_DATE) Omissible<Optional<ZonedDateTime>> startDate,
        @JsonProperty(STOP_DATE) Omissible<Optional<ZonedDateTime>> stopDate,
        @JsonProperty(PAUSE_DATE) Omissible<Optional<ZonedDateTime>> pauseDate,
        @JsonProperty(END_DATE) Omissible<Optional<ZonedDateTime>> endDate) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.pauseDate = pauseDate;
        this.endDate = endDate;
    }

    @JsonProperty(START_DATE)
    public Omissible<Optional<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    @JsonProperty(STOP_DATE)
    public Omissible<Optional<ZonedDateTime>> getStopDate() {
        return stopDate;
    }

    @JsonProperty(PAUSE_DATE)
    public Omissible<Optional<ZonedDateTime>> getPauseDate() {
        return pauseDate;
    }

    @JsonProperty(END_DATE)
    public Omissible<Optional<ZonedDateTime>> getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<ZonedDateTime>> startDate = Omissible.omitted();
        private Omissible<Optional<ZonedDateTime>> stopDate = Omissible.omitted();
        private Omissible<Optional<ZonedDateTime>> pauseDate = Omissible.omitted();
        private Omissible<Optional<ZonedDateTime>> endDate = Omissible.omitted();

        private Builder() {
        }

        public Builder withStartDate(ZonedDateTime startDate) {
            this.startDate = Omissible.of(Optional.ofNullable(startDate));
            return this;
        }

        public Builder withStopDate(ZonedDateTime stopDate) {
            this.stopDate = Omissible.of(Optional.ofNullable(stopDate));
            return this;
        }

        public Builder withPauseDate(ZonedDateTime pauseDate) {
            this.pauseDate = Omissible.of(Optional.ofNullable(pauseDate));
            return this;
        }

        public Builder withEndDate(ZonedDateTime endDate) {
            this.endDate = Omissible.of(Optional.ofNullable(endDate));
            return this;
        }

        public CampaignScheduleRequest build() {
            return new CampaignScheduleRequest(startDate, stopDate, pauseDate, endDate);
        }
    }
}
