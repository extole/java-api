package com.extole.email.rest.audit;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class EmailSendResponse {
    private static final String JSON_EMAIL_ID = "email_id";
    private static final String JSON_ACTION_ID = "action_id";
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_PERSON_ID = "person_id";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_ZONE_NAME = "zone_name";
    private static final String JSON_PROGRAM_ID = "program_id";
    private static final String JSON_ATTEMPTS = "attempts";
    private static final String JSON_DATA = "data";
    private static final String JSON_ATTACHMENTS = "attachments";
    private static final String JSON_RESULT = "result";

    private final String emailId;
    private final String actionId;
    private final String clientId;
    private final String personId;
    private final ZonedDateTime createdDate;
    private final String zoneName;
    private final String programId;
    private final Integer attempts;
    private final Map<String, String> data;
    private final Map<String, Id<?>> attachments;
    private final EmailSendResultResponse result;

    @JsonCreator
    public EmailSendResponse(
        @JsonProperty(JSON_EMAIL_ID) String emailId,
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_CLIENT_ID) String clientId,
        @JsonProperty(JSON_PERSON_ID) String personId,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_ZONE_NAME) String zoneName,
        @JsonProperty(JSON_PROGRAM_ID) String programId,
        @JsonProperty(JSON_ATTEMPTS) Integer attempts,
        @JsonProperty(JSON_DATA) Map<String, String> data,
        @JsonProperty(JSON_ATTACHMENTS) Map<String, Id<?>> attachments,
        @JsonProperty(JSON_RESULT) EmailSendResultResponse result) {
        this.emailId = emailId;
        this.actionId = actionId;
        this.clientId = clientId;
        this.personId = personId;
        this.createdDate = createdDate;
        this.zoneName = zoneName;
        this.programId = programId;
        this.attempts = attempts;
        this.data = data;
        this.attachments = attachments;
        this.result = result;
    }

    @JsonProperty(JSON_EMAIL_ID)
    public String getEmailId() {
        return emailId;
    }

    @JsonProperty(JSON_ACTION_ID)
    public String getActionId() {
        return actionId;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_PERSON_ID)
    public String getPersonId() {
        return personId;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_ZONE_NAME)
    public Optional<String> getZoneName() {
        return Optional.ofNullable(zoneName);
    }

    @JsonProperty(JSON_PROGRAM_ID)
    public Optional<String> getProgramId() {
        return Optional.ofNullable(programId);
    }

    @JsonProperty(JSON_ATTEMPTS)
    public Integer getAttempts() {
        return attempts;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(JSON_ATTACHMENTS)
    public Map<String, Id<?>> getAttachments() {
        return attachments;
    }

    @JsonProperty(JSON_RESULT)
    public Optional<EmailSendResultResponse> getResult() {
        return Optional.ofNullable(result);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
