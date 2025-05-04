package com.extole.consumer.rest.share.custom;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CustomShareRequest {
    private final String advocateCode;
    private final String channel;
    private final String message;
    private final String recipientEmail;
    private final Map<String, String> data;

    @JsonCreator
    public CustomShareRequest(
        @JsonProperty("advocate_code") String advocateCode,
        @JsonProperty("channel") String channel,
        @JsonProperty(value = "message", required = false) String message,
        @JsonProperty(value = "recipient_email", required = false) String recipientEmail,
        @JsonProperty(value = "data", required = false) Map<String, String> data) {
        this.advocateCode = advocateCode;
        this.channel = channel;
        this.message = message;
        this.recipientEmail = recipientEmail;
        this.data = data;
    }

    @JsonProperty("advocate_code")
    public String getAdvocateCode() {
        return advocateCode;
    }

    @JsonProperty("channel")
    public String getChannel() {
        return channel;
    }

    @JsonProperty("message")
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @JsonProperty("recipient_email")
    public Optional<String> getRecipientEmail() {
        return Optional.ofNullable(recipientEmail);
    }

    @JsonProperty("data")
    public Optional<Map<String, String>> getData() {
        return Optional.ofNullable(data);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
