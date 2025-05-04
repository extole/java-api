package com.extole.consumer.rest.share.email;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class EmailShareRequest {
    private static final String PREFERRED_CODE_PREFIXES = "preferred_code_prefixes";
    private static final String KEY = "key";
    private static final String LABELS = "labels";
    private static final String CAMPAIGN_ID = "campaign_id";

    private static final String RECIPIENT_EMAIL = "recipient_email";
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String DATA = "data";

    private final List<String> preferredCodePrefixes;
    private final String key;
    private final String labels;
    private final String campaignId;

    private final String message;
    private final String subject;
    private final String recipientEmail;
    private final Map<String, String> data;

    @JsonCreator
    public EmailShareRequest(
        @Nullable @JsonProperty(PREFERRED_CODE_PREFIXES) List<String> preferredCodePrefixes,
        @Nullable @JsonProperty(KEY) String key,
        @Nullable @JsonProperty(LABELS) String labels,
        @Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,

        @JsonProperty(RECIPIENT_EMAIL) String recipientEmail,
        @Nullable @JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(value = SUBJECT) String subject,
        @JsonProperty(value = DATA) Map<String, String> data) {
        this.preferredCodePrefixes = preferredCodePrefixes;
        this.key = key;
        this.labels = labels;
        this.campaignId = campaignId;
        this.recipientEmail = recipientEmail;
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

    @JsonProperty(RECIPIENT_EMAIL)
    public String getRecipientEmail() {
        return recipientEmail;
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

        private Builder() {
        }

        private List<String> preferredCodePrefixes;
        private String key;
        private String labels;
        private String campaignId;

        private String message;
        private String subject;
        private String recipientEmail;
        private Map<String, String> data;

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withRecipientEmail(String recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public Builder addData(Map<String, String> data) {
            if (this.data != null) {
                this.data.putAll(data);
            } else {
                this.data = data;
            }
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = data;
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

        public EmailShareRequest build() {
            return new EmailShareRequest(preferredCodePrefixes, key,
                labels, campaignId, recipientEmail, message, subject, data);
        }
    }
}
