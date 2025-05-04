package com.extole.client.rest.person.v4;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonStepsV4ListRequest {

    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_CONTAINER = "container";
    public static final String PARAMETER_CAMPAIGN_ID = "campaign_id";
    public static final String PARAMETER_PROGRAM_LABEL = "program_label";
    public static final String PARAMETER_PARTNER_ID = "partner_id";
    public static final String PARAMETER_FLOW_PATH = "flow_path";
    public static final String PARAMETER_VISIT_TYPE = "visit_type";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_EVENT_ID = "event_id";

    private final String stepName;
    private final String container;
    private final String campaignId;
    private final String programLabel;
    private final String partnerEventId;
    private final String flowPath;
    private final String visitType;
    private final Integer offset;
    private final Integer limit;
    private final String eventId;

    public PersonStepsV4ListRequest(
        @Parameter(description = "Optional step name filter.")
        @Nullable @QueryParam(PARAMETER_NAME) String stepName,
        @Parameter(description = "Optional container filter, defaults to production container. " +
            "Pass \"*\" to include steps for all containers.")
        @Nullable @QueryParam(PARAMETER_CONTAINER) String container,
        @Parameter(description = "Optional campaign id filter.")
        @Nullable @QueryParam(PARAMETER_CAMPAIGN_ID) String campaignId,
        @Parameter(description = "Optional program label filter.")
        @Nullable @QueryParam(PARAMETER_PROGRAM_LABEL) String programLabel,
        @Parameter(description = "Optional partner id filter, using this format: <name>:<value>.")
        @Nullable @QueryParam(PARAMETER_PARTNER_ID) String partnerEventId,
        @Parameter(description = "Optional flow path filter.")
        @Nullable @QueryParam(PARAMETER_FLOW_PATH) String flowPath,
        @Parameter(description = "Optional visit type filter. One of: LAST_VISITED")
        @Nullable @QueryParam(PARAMETER_VISIT_TYPE) String visitType,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @Nullable @QueryParam(PARAMETER_OFFSET) Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @Nullable @QueryParam(PARAMETER_LIMIT) Integer limit,
        @Parameter(description = "Optional event id filter")
        @Nullable @QueryParam(PARAMETER_EVENT_ID) String eventId) {
        this.stepName = stepName;
        this.container = container;
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.partnerEventId = partnerEventId;
        this.flowPath = flowPath;
        this.visitType = visitType;
        this.offset = offset;
        this.limit = limit;
        this.eventId = eventId;
    }

    @Nullable
    @QueryParam(PARAMETER_NAME)
    public String getStepName() {
        return stepName;
    }

    @Nullable
    @QueryParam(PARAMETER_CONTAINER)
    public String getContainer() {
        return container;
    }

    @Nullable
    @QueryParam(PARAMETER_CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @Nullable
    @QueryParam(PARAMETER_PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @Nullable
    @QueryParam(PARAMETER_PARTNER_ID)
    public String getPartnerEventId() {
        return partnerEventId;
    }

    @Nullable
    @QueryParam(PARAMETER_FLOW_PATH)
    public String getFlowPath() {
        return flowPath;
    }

    @Nullable
    @QueryParam(PARAMETER_VISIT_TYPE)
    public String getVisitType() {
        return visitType;
    }

    @Nullable
    @QueryParam(PARAMETER_OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @Nullable
    @QueryParam(PARAMETER_LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @Nullable
    @QueryParam(PARAMETER_EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String stepName;
        private String container;
        private String campaignId;
        private String programLabel;
        private String partnerEventId;
        private String flowPath;
        private String visitType;
        private Integer offset;
        private Integer limit;
        private String eventId;

        private Builder() {
        }

        public Builder withName(String name) {
            this.stepName = name;
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public Builder withPartnerEventId(String partnerEventId) {
            this.partnerEventId = partnerEventId;
            return this;
        }

        public Builder withFlowPath(String flowPath) {
            this.flowPath = flowPath;
            return this;
        }

        public Builder withVisiType(String visitType) {
            this.visitType = visitType;
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public PersonStepsV4ListRequest build() {
            return new PersonStepsV4ListRequest(stepName, container, campaignId, programLabel, partnerEventId, flowPath,
                visitType, offset, limit, eventId);
        }

    }

}
