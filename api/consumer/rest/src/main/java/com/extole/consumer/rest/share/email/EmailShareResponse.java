package com.extole.consumer.rest.share.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailShareResponse {
    private final String pollingId;
    private final String recepientEmail;

    @JsonCreator
    public EmailShareResponse(@JsonProperty("recipient_email") String recepientEmail,
        @JsonProperty("polling_id") String pollingId) {
        this.pollingId = pollingId;
        this.recepientEmail = recepientEmail;
    }

    @JsonProperty("polling_id")
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty("recipient_email")
    public String getRecipientEmail() {
        return recepientEmail;
    }
}
