package com.extole.reporting.rest.report;

import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public class DimensionStatsGetRequest {

    private final Optional<String> campaignId;
    private final Optional<String> programLabel;
    private final Optional<String> container;
    private final Optional<DimensionTimePeriod> timePeriod;
    private final Optional<Integer> limit;
    private final Optional<Integer> offset;

    public DimensionStatsGetRequest(
        @QueryParam("campaign_id") Optional<String> campaignId,
        @QueryParam("program_label") Optional<String> programLabel,
        @QueryParam("container") Optional<String> container,
        @QueryParam("time_period") Optional<DimensionTimePeriod> timePeriod,
        @DefaultValue("5") @QueryParam("limit") Optional<Integer> limit,
        @DefaultValue("0") @QueryParam("offset") Optional<Integer> offset) {
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.container = container;
        this.timePeriod = timePeriod;
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam("campaign_id")
    public Optional<String> getCampaignId() {
        return campaignId;
    }

    @QueryParam("program_label")
    public Optional<String> getProgramLabel() {
        return programLabel;
    }

    @QueryParam("container")
    public Optional<String> getContainer() {
        return container;
    }

    @QueryParam("time_period")
    public Optional<DimensionTimePeriod> getTimePeriod() {
        return timePeriod;
    }

    @QueryParam("limit")
    public Optional<Integer> getLimit() {
        return limit;
    }

    @QueryParam("offset")
    public Optional<Integer> getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<String> campaignId = Optional.empty();
        private Optional<String> programLabel = Optional.empty();
        private Optional<String> container = Optional.empty();
        private Optional<DimensionTimePeriod> timePeriod = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private Optional<Integer> offset = Optional.empty();

        private Builder() {
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = Optional.ofNullable(campaignId);
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = Optional.ofNullable(programLabel);
            return this;
        }

        public Builder withContainer(String container) {
            this.container = Optional.ofNullable(container);
            return this;
        }

        public Builder withTimePeriod(DimensionTimePeriod timePeriod) {
            this.timePeriod = Optional.ofNullable(timePeriod);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public DimensionStatsGetRequest build() {
            return new DimensionStatsGetRequest(campaignId, programLabel, container, timePeriod, limit, offset);
        }
    }
}
