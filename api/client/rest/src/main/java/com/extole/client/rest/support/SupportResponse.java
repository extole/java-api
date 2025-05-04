package com.extole.client.rest.support;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SupportResponse {
    private static final String CSM_USER_ID = "csm_user_id";
    private static final String SUPPORT_USER_ID = "support_user_id";
    private static final String SLACK_CHANNEL_NAME = "slack_channel_name";
    private static final String EXTERNAL_SLACK_CHANNEL_NAME = "external_slack_channel_name";
    private static final String SALESFORCE_ACCOUNT_ID = "salesforce_account_id";
    private static final String NOTES = "notes";

    private final Optional<String> csmUserId;
    private final Optional<String> supportUserId;
    private final Optional<String> slackChannelName;
    private final Optional<String> externalSlackChannelName;
    private final Optional<String> salesforceAccountId;
    private final Optional<String> notes;

    @JsonCreator
    public SupportResponse(
        @JsonProperty(CSM_USER_ID) Optional<String> csmUserId,
        @JsonProperty(SUPPORT_USER_ID) Optional<String> supportUserId,
        @JsonProperty(SLACK_CHANNEL_NAME) Optional<String> slackChannelName,
        @JsonProperty(EXTERNAL_SLACK_CHANNEL_NAME) Optional<String> externalSlackChannelName,
        @JsonProperty(SALESFORCE_ACCOUNT_ID) Optional<String> salesforceAccountId,
        @JsonProperty(NOTES) Optional<String> notes) {
        this.csmUserId = csmUserId;
        this.supportUserId = supportUserId;
        this.slackChannelName = slackChannelName;
        this.externalSlackChannelName = externalSlackChannelName;
        this.salesforceAccountId = salesforceAccountId;
        this.notes = notes;
    }

    @JsonProperty(CSM_USER_ID)
    public Optional<String> getCsmUserId() {
        return csmUserId;
    }

    @JsonProperty(SUPPORT_USER_ID)
    public Optional<String> getSupportUserId() {
        return supportUserId;
    }

    @JsonProperty(SLACK_CHANNEL_NAME)
    public Optional<String> getSlackChannelName() {
        return slackChannelName;
    }

    @JsonProperty(EXTERNAL_SLACK_CHANNEL_NAME)
    public Optional<String> getExternalSlackChannelName() {
        return externalSlackChannelName;
    }

    @JsonProperty(SALESFORCE_ACCOUNT_ID)
    public Optional<String> getSalesforceAccountId() {
        return salesforceAccountId;
    }

    @JsonProperty(NOTES)
    public Optional<String> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
