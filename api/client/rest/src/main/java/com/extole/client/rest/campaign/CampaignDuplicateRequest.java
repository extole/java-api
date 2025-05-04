package com.extole.client.rest.campaign;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CampaignDuplicateRequest {
    private static final String PROGRAM_LABEL = "program_label";
    private static final String MESSAGE = "message";
    private static final String DESCRIPTION = "description";

    private final String programLabel;
    private final String message;
    private final String description;

    public CampaignDuplicateRequest(@Nullable @JsonProperty(PROGRAM_LABEL) String programLabel,
        @Nullable @JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(DESCRIPTION) String description) {
        this.programLabel = programLabel;
        this.message = message;
        this.description = description;
    }

    @Nullable
    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Nullable
    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static CampaignDuplicateRequestBuilder builder() {
        return new CampaignDuplicateRequestBuilder();
    }

    public static final class CampaignDuplicateRequestBuilder {
        private String programLabel;
        private String message;
        private String description;

        private CampaignDuplicateRequestBuilder() {
        }

        public CampaignDuplicateRequestBuilder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public CampaignDuplicateRequestBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public CampaignDuplicateRequestBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CampaignDuplicateRequest build() {
            return new CampaignDuplicateRequest(programLabel, message, description);
        }
    }

}
