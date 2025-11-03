package com.extole.client.rest.schedule;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;

public class ScheduleListRequest {
    private final Optional<ScheduleStatus> status;
    private final Optional<String> scheduleName;
    private final Optional<String> personId;
    private final Optional<String> personIdentityId;
    private final Optional<ZonedDateTime> scheduledExecutionDateFrom;
    private final Optional<ZonedDateTime> scheduledExecutionDateTo;
    private final Optional<Integer> limit;
    private final ZoneId timeZone;

    public ScheduleListRequest(
        @Parameter(
            description = "Schedule status, defaults to SCHEDULED") @QueryParam("status") Optional<
                ScheduleStatus> status,
        @Parameter(description = "Schedule name") @QueryParam("schedule_name") Optional<String> scheduleName,
        @Parameter(description = "Person id") @QueryParam("person_id") Optional<String> personId,
        @Parameter(
            description = "Person identity id") @QueryParam("person_identity_id") Optional<String> personIdentityId,
        @Parameter(
            description = "Scheduled execution date from. Defaults to now") @QueryParam("scheduled_execution_date_from") Optional<
                ZonedDateTime> scheduledExecutionDateFrom,
        @Parameter(description = "Scheduled execution date to."
            + " Defaults to last 30 days when person is specified, otherwise 24 hours") @QueryParam("scheduled_execution_date_to") Optional<
                ZonedDateTime> scheduledExecutionDateTo,
        @Parameter(description = "Limit, defaults to 100. Max limit 1000") @QueryParam("limit") Optional<Integer> limit,
        @Nullable @TimeZoneParam ZoneId timeZone) {
        this.status = status;
        this.scheduleName = scheduleName;
        this.personId = personId;
        this.personIdentityId = personIdentityId;
        this.scheduledExecutionDateFrom = scheduledExecutionDateFrom;
        this.scheduledExecutionDateTo = scheduledExecutionDateTo;
        this.limit = limit;
        this.timeZone = timeZone;
    }

    @QueryParam("status")
    public Optional<ScheduleStatus> getStatus() {
        return this.status;
    }

    @QueryParam("schedule_name")
    public Optional<String> getScheduleName() {
        return this.scheduleName;
    }

    @QueryParam("person_id")
    public Optional<String> getPersonId() {
        return this.personId;
    }

    @QueryParam("person_identity_id")
    public Optional<String> getPersonIdentityId() {
        return this.personIdentityId;
    }

    @QueryParam("scheduled_execution_date_from")
    public Optional<ZonedDateTime> getScheduledExecutionDateFrom() {
        return this.scheduledExecutionDateFrom;
    }

    @QueryParam("scheduled_execution_date_to")
    public Optional<ZonedDateTime> getScheduledExecutionDateTo() {
        return this.scheduledExecutionDateTo;
    }

    @QueryParam("limit")
    public Optional<Integer> getLimit() {
        return this.limit;
    }

    @TimeZoneParam
    public ZoneId getTimeZone() {
        return this.timeZone;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ScheduleStatus status;
        private String scheduleName;
        private String personId;
        private String personIdentityId;
        private ZonedDateTime scheduledExecutionDateFrom;
        private ZonedDateTime scheduledExecutionDateTo;
        private Integer limit;
        private ZoneId timeZone;

        public Builder withStatus(ScheduleStatus status) {
            this.status = status;
            return this;
        }

        public Builder withScheduleName(String scheduleName) {
            this.scheduleName = scheduleName;
            return this;
        }

        public Builder withPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPersonIdentityId(String personIdentityId) {
            this.personIdentityId = personIdentityId;
            return this;
        }

        public Builder withScheduledExecutionDateFrom(ZonedDateTime scheduledExecutionDateFrom) {
            this.scheduledExecutionDateFrom = scheduledExecutionDateFrom;
            return this;
        }

        public Builder withScheduledExecutionDateTo(ZonedDateTime scheduledExecutionDateTo) {
            this.scheduledExecutionDateTo = scheduledExecutionDateTo;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withTimeZone(ZoneId timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public ScheduleListRequest build() {
            return new ScheduleListRequest(
                Optional.ofNullable(status),
                Optional.ofNullable(scheduleName),
                Optional.ofNullable(personId),
                Optional.ofNullable(personIdentityId),
                Optional.ofNullable(scheduledExecutionDateFrom),
                Optional.ofNullable(scheduledExecutionDateTo),
                Optional.ofNullable(limit),
                timeZone);
        }
    }
}
