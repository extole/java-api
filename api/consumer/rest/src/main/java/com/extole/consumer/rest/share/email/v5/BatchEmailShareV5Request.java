package com.extole.consumer.rest.share.email.v5;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BatchEmailShareV5Request {
    private static final String ADVOCATE_CODE = "advocate_code";
    private static final String RECIPIENT_EMAILS = "recipient_emails";
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String DATA = "data";

    private final String message;
    private final String subject;
    private final List<String> recipientEmails;
    private final String advocateCode;
    private final Map<String, String> data;

    @JsonCreator
    public BatchEmailShareV5Request(
        @JsonProperty(ADVOCATE_CODE) String shareableCode,
        @JsonProperty(RECIPIENT_EMAILS) List<String> recipientEmails,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(value = SUBJECT, required = false) String subject,
        @JsonProperty(value = DATA, required = false) Map<String, String> data) {
        this.advocateCode = shareableCode;
        this.recipientEmails = recipientEmails;
        this.message = message;
        this.subject = subject;
        this.data = data;
    }

    @JsonProperty(ADVOCATE_CODE)
    public String getAdvocateCode() {
        return advocateCode;
    }

    @JsonProperty(RECIPIENT_EMAILS)
    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Nullable
    @JsonProperty(SUBJECT)
    public String getSubject() {
        return subject;
    }

    @Nullable
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
        private String message;
        private String subject;
        private List<String> recipientEmails;
        private String advocateCode;
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

        public Builder withRecipientEmails(List<String> recipientEmails) {
            this.recipientEmails = recipientEmails;
            return this;
        }

        public Builder withAdvocateCode(String advocateCode) {
            this.advocateCode = advocateCode;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public BatchEmailShareV5Request build() {
            return new BatchEmailShareV5Request(advocateCode, recipientEmails, message, subject, data);
        }
    }
}
