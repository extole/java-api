package com.extole.consumer.rest.share.event;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@Deprecated // TODO remove ENG-10140
public class EventShareRequest {
    private final String shareableId;
    private final String message;
    private final ConsumerEventRequest consumerEvent;
    private final String recipientEmail;
    private final String campaignId;
    private final Map<String, String> data;

    @JsonCreator
    public EventShareRequest(
        @JsonProperty("shareable_id") String shareableId,
        @JsonProperty("message") String message,
        @JsonProperty(value = "recipient_email", required = false) String recipientEmail,
        @JsonProperty(value = "campaign_id", required = false) String campaignId,
        @JsonProperty(value = "data", required = false) Map<String, String> data,
        @JsonProperty(value = "consumer_event", required = false) ConsumerEventRequest consumerEvent) {
        this.shareableId = shareableId;
        this.message = message;
        this.consumerEvent = consumerEvent;
        this.recipientEmail = recipientEmail;
        this.campaignId = campaignId;
        this.data = data;
    }

    public EventShareRequest(String shareableId,
        String message,
        ConsumerEventRequest consumerEvent) {
        this.shareableId = shareableId;
        this.message = message;
        this.consumerEvent = consumerEvent;
        this.recipientEmail = null;
        this.campaignId = null;
        this.data = null;
    }

    @JsonProperty("shareable_id")
    public String getShareableId() {
        return shareableId;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @Nullable
    @JsonProperty("consumer_event")
    public ConsumerEventRequest getConsumerEvent() {
        return consumerEvent;
    }

    @JsonProperty("campaign_id")
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty("recipient_email")
    public String getRecipientEmail() {
        return recipientEmail;
    }

    @JsonProperty("data")
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
