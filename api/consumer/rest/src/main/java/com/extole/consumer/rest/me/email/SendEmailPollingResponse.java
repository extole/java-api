package com.extole.consumer.rest.me.email;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

public class SendEmailPollingResponse implements PollingResponse {
    private static final String ERROR = "error";
    private static final String EMAIL_ID = "email_id";
    private static final String ACTION_ID = "action_id";

    private final String pollingId;
    private final PollingStatus status;
    private final String emailId;
    private final String actionId;
    private final SendEmailError error;

    public SendEmailPollingResponse(
        @JsonProperty(POLLING_ID) String pollingId,
        @JsonProperty(STATUS) PollingStatus status,
        @JsonProperty(EMAIL_ID) String emailId,
        @JsonProperty(ACTION_ID) String actionId,
        @JsonProperty(ERROR) SendEmailError error) {
        this.pollingId = pollingId;
        this.status = status;
        this.emailId = emailId;
        this.actionId = actionId;
        this.error = error;
    }

    @Override
    @JsonProperty(POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @Override
    @JsonProperty(STATUS)
    public PollingStatus getStatus() {
        return status;
    }

    @Nullable
    @JsonProperty(EMAIL_ID)
    public String getEmailId() {
        return emailId;
    }

    @Nullable
    @JsonProperty(ACTION_ID)
    public String getActionId() {
        return actionId;
    }

    @Nullable
    @JsonProperty(ERROR)
    public SendEmailError getError() {
        return error;
    }
}
