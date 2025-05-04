package com.extole.consumer.rest.share.email;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BatchEmailShareRequest {
    private static final String PREFERRED_CODE_PREFIXES = "preferred_code_prefixes";
    private static final String KEY = "key";
    private static final String LABELS = "labels";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String RECIPIENT_EMAILS = "recipient_emails";
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String DATA = "data";

    private final List<String> preferredCodePrefixes;
    private final String key;
    private final String labels;
    private final String campaignId;
    private final String message;
    private final String subject;
    private final List<String> recipientEmails;
    private final Map<String, String> data;

    @JsonCreator
    public BatchEmailShareRequest(
        @Nullable @JsonProperty(PREFERRED_CODE_PREFIXES) List<String> preferredCodePrefixes,
        @Nullable @JsonProperty(KEY) String key,
        @Nullable @JsonProperty(LABELS) String labels,
        @Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(RECIPIENT_EMAILS) List<String> recipientEmails,
        @Nullable @JsonProperty(MESSAGE) String message,
        @JsonProperty(value = SUBJECT) String subject,
        @JsonProperty(value = DATA) Map<String, String> data) {
        this.preferredCodePrefixes = preferredCodePrefixes;
        this.key = key;
        this.labels = labels;
        this.campaignId = campaignId;
        this.recipientEmails = recipientEmails;
        this.message = message;
        this.subject = subject;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    @Nullable
    @JsonProperty(PREFERRED_CODE_PREFIXES)
    public List<String> getPreferredCodePrefixes() {
        return preferredCodePrefixes;
    }

    @Nullable
    @JsonProperty(KEY)
    public String getKey() {
        return key;
    }

    @Nullable
    @JsonProperty(LABELS)
    public String getLabels() {
        return labels;
    }

    @Nullable
    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(RECIPIENT_EMAILS)
    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Nullable
    @JsonProperty(SUBJECT)
    public String getSubject() {
        return subject;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<String> preferredCodePrefixes;
        private String key;
        private String labels;
        private String campaignId;
        private String message;
        private String subject;
        private List<String> recipientEmails;
        private Map<String, String> data;

        private Builder() {
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withRecipientEmails(List<String> recipientEmail) {
            this.recipientEmails = recipientEmail;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            if (this.data != null) {
                this.data.putAll(data);
            } else {
                this.data = data;
            }
            return this;
        }

        public Builder withKey(String key) {
            this.key = key;
            return this;
        }

        public Builder withPreferredCodePrefixes(List<String> preferredCodePrefixes) {
            this.preferredCodePrefixes = preferredCodePrefixes;
            return this;
        }

        public Builder withLabels(String labels) {
            this.labels = labels;
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public BatchEmailShareRequest build() {
            return new BatchEmailShareRequest(preferredCodePrefixes, key, labels, campaignId, recipientEmails, message,
                subject, data);
        }
    }
}
