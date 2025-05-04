package com.extole.client.rest.campaign;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class CampaignScheduleStateRequest {

    private static final String DATE = "date";

    private final Omissible<Optional<ZonedDateTime>> date;

    public CampaignScheduleStateRequest(@JsonProperty(DATE) Omissible<Optional<ZonedDateTime>> date) {

        this.date = date;
    }

    @JsonProperty(DATE)
    public Omissible<Optional<ZonedDateTime>> getScheduledDate() {
        return date;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<ZonedDateTime>> date = Omissible.omitted();

        private Builder() {
        }

        public Builder withScheduleDate(ZonedDateTime date) {
            this.date = Omissible.of(Optional.ofNullable(date));
            return this;
        }

        public CampaignScheduleStateRequest build() {
            return new CampaignScheduleStateRequest(date);
        }
    }
}
