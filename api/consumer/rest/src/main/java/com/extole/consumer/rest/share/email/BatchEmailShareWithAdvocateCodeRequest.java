package com.extole.consumer.rest.share.email;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BatchEmailShareWithAdvocateCodeRequest {
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String ADVOCATE_CODE = "advocate_code";
    private static final String RECIPIENT_EMAILS = "recipient_emails";
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String DATA = "data";

    private String campaignId;
    private String advocateCode;
    private final String message;
    private final String subject;
    private final List<String> recipientEmails;
    private final Map<String, String> data;

    @JsonCreator
    public BatchEmailShareWithAdvocateCodeRequest(
        @Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(ADVOCATE_CODE) String advocateCode,
        @JsonProperty(RECIPIENT_EMAILS) List<String> recipientEmails,
        @Nullable @JsonProperty(MESSAGE) String message,
        @JsonProperty(value = SUBJECT) String subject,
        @JsonProperty(value = DATA) Map<String, String> data) {
        this.campaignId = campaignId;
        this.advocateCode = advocateCode;
        this.recipientEmails = recipientEmails;
        this.message = message;
        this.subject = subject;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    @Nullable
    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(ADVOCATE_CODE)
    public String getAdvocateCode() {
        return advocateCode;
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

        private Builder() {
        }

        private String campaignId;
        private String advocateCode;

        private String message;
        private String subject;
        private List<String> recipientEmails;
        private Map<String, String> data;

        public Builder withAdvocateCode(String advocateCode) {
            this.advocateCode = advocateCode;
            return this;
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

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public BatchEmailShareWithAdvocateCodeRequest build() {
            return new BatchEmailShareWithAdvocateCodeRequest(campaignId, advocateCode, recipientEmails, message,
                subject, data);
        }
    }
}
