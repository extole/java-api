package com.extole.consumer.rest.share.email;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class EmailShareWithAdvocateCodeRequest {
    private static final String CAMPAIGN_ID = "campaign_id";

    private static final String ADVOCATE_CODE = "advocate_code";
    private static final String RECIPIENT_EMAIL = "recipient_email";
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String DATA = "data";

    private final String campaignId;
    private final String advocateCode;

    private final String message;
    private final String subject;
    private final String recipientEmail;
    private final Map<String, String> data;

    @JsonCreator
    public EmailShareWithAdvocateCodeRequest(
        @Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(ADVOCATE_CODE) String advocateCode,
        @JsonProperty(RECIPIENT_EMAIL) String recipientEmail,
        @Nullable @JsonProperty(MESSAGE) String message,
        @JsonProperty(value = SUBJECT) String subject,
        @JsonProperty(value = DATA) Map<String, String> data) {
        this.campaignId = campaignId;
        this.advocateCode = advocateCode;
        this.recipientEmail = recipientEmail;
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

        private String campaignId;

        private String advocateCode;
        private String message;
        private String subject;
        private String recipientEmail;
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

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public EmailShareWithAdvocateCodeRequest build() {
            return new EmailShareWithAdvocateCodeRequest(campaignId, advocateCode, recipientEmail, message, subject,
                data);
        }
    }
}
