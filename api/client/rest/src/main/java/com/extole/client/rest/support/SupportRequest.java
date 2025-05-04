package com.extole.client.rest.support;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class SupportRequest {
    private static final String CSM_USER_ID = "csm_user_id";
    private static final String SUPPORT_USER_ID = "support_user_id";
    private static final String SLACK_CHANNEL_NAME = "slack_channel_name";
    private static final String EXTERNAL_SLACK_CHANNEL_NAME = "external_slack_channel_name";
    private static final String SALESFORCE_ACCOUNT_ID = "salesforce_account_id";

    private static final String NOTES = "notes";

    private final Omissible<Optional<String>> csmUserId;
    private final Omissible<Optional<String>> supportUserId;
    private final Omissible<Optional<String>> slackChannelName;
    private final Omissible<Optional<String>> externalSlackChannelName;
    private final Omissible<Optional<String>> salesforceAccountId;
    private final Omissible<Optional<String>> notes;

    @JsonCreator
    public SupportRequest(
        @JsonProperty(CSM_USER_ID) Omissible<Optional<String>> csmUserId,
        @JsonProperty(SUPPORT_USER_ID) Omissible<Optional<String>> supportUserId,
        @JsonProperty(SLACK_CHANNEL_NAME) Omissible<Optional<String>> slackChannelName,
        @JsonProperty(EXTERNAL_SLACK_CHANNEL_NAME) Omissible<Optional<String>> externalSlackChannelName,
        @JsonProperty(SALESFORCE_ACCOUNT_ID) Omissible<Optional<String>> salesforceAccountId,
        @JsonProperty(NOTES) Omissible<Optional<String>> notes) {
        this.csmUserId = csmUserId;
        this.supportUserId = supportUserId;
        this.slackChannelName = slackChannelName;
        this.externalSlackChannelName = externalSlackChannelName;
        this.salesforceAccountId = salesforceAccountId;
        this.notes = notes;
    }

    @JsonProperty(CSM_USER_ID)
    public Omissible<Optional<String>> getCsmUserId() {
        return csmUserId;
    }

    @JsonProperty(SUPPORT_USER_ID)
    public Omissible<Optional<String>> getSupportUserId() {
        return supportUserId;
    }

    @JsonProperty(SLACK_CHANNEL_NAME)
    public Omissible<Optional<String>> getSlackChannelName() {
        return slackChannelName;
    }

    @JsonProperty(EXTERNAL_SLACK_CHANNEL_NAME)
    public Omissible<Optional<String>> getExternalSlackChannelName() {
        return externalSlackChannelName;
    }

    @JsonProperty(SALESFORCE_ACCOUNT_ID)
    public Omissible<Optional<String>> getSalesforceAccountId() {
        return salesforceAccountId;
    }

    @JsonProperty(NOTES)
    public Omissible<Optional<String>> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> csmUserId = Omissible.omitted();
        private Omissible<Optional<String>> supportUserId = Omissible.omitted();
        private Omissible<Optional<String>> slackChannelName = Omissible.omitted();
        private Omissible<Optional<String>> externalSlackChannelName = Omissible.omitted();
        private Omissible<Optional<String>> salesforceAccountId = Omissible.omitted();
        private Omissible<Optional<String>> notes = Omissible.omitted();

        private Builder() {
        }

        public Builder withCsmUserId(String csmUserId) {
            this.csmUserId = Omissible.of(Optional.ofNullable(csmUserId));
            return this;
        }

        public Builder withSupportUserId(String supportUserId) {
            this.supportUserId = Omissible.of(Optional.ofNullable(supportUserId));
            return this;
        }

        public Builder withSlackChannelName(String slackChannelName) {
            this.slackChannelName = Omissible.of(Optional.ofNullable(slackChannelName));
            return this;
        }

        public Builder withExternalSlackChannelName(String slackChannelName) {
            this.externalSlackChannelName = Omissible.of(Optional.ofNullable(slackChannelName));
            return this;
        }

        public Builder withSalesforceAccountId(String salesforceAccountId) {
            this.salesforceAccountId = Omissible.of(Optional.ofNullable(salesforceAccountId));
            return this;
        }

        public Builder withNotes(String notes) {
            this.notes = Omissible.of(Optional.ofNullable(notes));
            return this;
        }

        public SupportRequest build() {
            return new SupportRequest(csmUserId, supportUserId, slackChannelName, externalSlackChannelName,
                salesforceAccountId, notes);
        }
    }
}
