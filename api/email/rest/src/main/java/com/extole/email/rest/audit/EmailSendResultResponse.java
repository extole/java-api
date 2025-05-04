package com.extole.email.rest.audit;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailSendResultResponse {

    private static final String JSON_STATUS = "status";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_NORMALIZED_EMAIL_TO = "normalized_email_to";
    private static final String JSON_NORMALIZED_EMAIL_FROM = "normalized_email_from";
    private static final String JSON_NORMALIZED_EMAIL_SENDER = "normalized_email_sender";
    private static final String JSON_EMAIL_SENT_AS = "email_sent_as";
    private static final String JSON_RECIPIENT_TYPE = "recipient_type";
    private final Status status;
    private final ZonedDateTime createdDate;
    private final String normalizedEmailTo;
    private final String normalizedEmailFrom;
    private final String normalizedEmailSender;
    private final String emailSentAs;
    private final RecipientType recipientType;

    @JsonCreator
    public EmailSendResultResponse(
        @JsonProperty(JSON_STATUS) Status status,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @Nullable @JsonProperty(JSON_NORMALIZED_EMAIL_FROM) String normalizedEmailFrom,
        @Nullable @JsonProperty(JSON_NORMALIZED_EMAIL_TO) String normalizedEmailTo,
        @Nullable @JsonProperty(JSON_NORMALIZED_EMAIL_SENDER) String normalizedEmailSender,
        @Nullable @JsonProperty(JSON_EMAIL_SENT_AS) String emailSentAs,
        @Nullable @JsonProperty(JSON_RECIPIENT_TYPE) RecipientType recipientType) {
        this.status = status;
        this.createdDate = createdDate;
        this.normalizedEmailFrom = normalizedEmailFrom;
        this.normalizedEmailTo = normalizedEmailTo;
        this.normalizedEmailSender = normalizedEmailSender;
        this.emailSentAs = emailSentAs;
        this.recipientType = recipientType;
    }

    @JsonProperty(JSON_STATUS)
    public Status getStatus() {
        return status;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @Nullable
    @JsonProperty(JSON_NORMALIZED_EMAIL_FROM)
    public String getNormalizedEmailFrom() {
        return normalizedEmailFrom;
    }

    @Nullable
    @JsonProperty(JSON_NORMALIZED_EMAIL_TO)
    public String getNormalizedEmailTo() {
        return normalizedEmailTo;
    }

    @Nullable
    @JsonProperty(JSON_NORMALIZED_EMAIL_SENDER)
    public String getNormalizedEmailSender() {
        return normalizedEmailSender;
    }

    @Nullable
    @JsonProperty(JSON_EMAIL_SENT_AS)
    public String getEmailSentAs() {
        return emailSentAs;
    }

    @JsonProperty(JSON_RECIPIENT_TYPE)
    public RecipientType getRecipientType() {
        return recipientType;
    }

    public enum Status {
        SUCCESS, FAILED, ABORTED
    }
}
