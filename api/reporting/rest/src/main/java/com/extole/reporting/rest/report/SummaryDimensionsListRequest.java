package com.extole.reporting.rest.report;

import java.util.Optional;

import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public class SummaryDimensionsListRequest {

    private final Optional<String> campaignId;
    private final Optional<String> programLabel;
    private final Optional<String> container;
    private final Optional<String> stepName;

    public SummaryDimensionsListRequest(
        @QueryParam("campaign_id") Optional<String> campaignId,
        @QueryParam("program_label") Optional<String> programLabel,
        @QueryParam("container") Optional<String> container,
        @QueryParam("step_name") Optional<String> stepName) {
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.container = container;
        this.stepName = stepName;
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

    @QueryParam("step_name")
    public Optional<String> getStepName() {
        return stepName;
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
        private Optional<String> stepName = Optional.empty();

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

        public Builder withStepName(String stepName) {
            this.stepName = Optional.ofNullable(stepName);
            return this;
        }

        public SummaryDimensionsListRequest build() {
            return new SummaryDimensionsListRequest(campaignId, programLabel, container, stepName);
        }
    }
}
