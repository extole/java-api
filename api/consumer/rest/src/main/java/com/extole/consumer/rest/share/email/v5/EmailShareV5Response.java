package com.extole.consumer.rest.share.email.v5;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailShareV5Response {
    private final String pollingId;
    private final String recepientEmail;

    @JsonCreator
    public EmailShareV5Response(
        @JsonProperty("recipient_email") String recepientEmail,
        @JsonProperty("polling_id") String pollingId) {
        this.pollingId = pollingId;
        this.recepientEmail = recepientEmail;
    }

    @JsonProperty("polling_id")
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty("recipient_email")
    public String getRecepientEmail() {
        return recepientEmail;
    }
}
