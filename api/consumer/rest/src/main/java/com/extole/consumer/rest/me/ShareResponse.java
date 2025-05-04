package com.extole.consumer.rest.me;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.person.PublicPersonResponse;

public class ShareResponse {

    private static final String ID = "id";
    private static final String SHARE_ID = "share_id";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String CHANNEL = "channel";
    private static final String SHARE_DATE = "share_date";
    private static final String LINK = "link";
    private static final String DATA = "data";
    private static final String PARTNER_ID = "partner_id";
    private static final String SHAREABLE_ID = "shareable_id";
    private static final String MESSAGE = "message";
    private static final String RECIPIENTS = "recipients";
    private static final String RECIPIENT_EMAIL = "recipient_email";
    private static final String FRIEND = "friend";
    private static final String SUBJECT = "subject";

    private final String id;
    private final String campaignId;
    private final String channel;
    private final String shareDate;
    private final String link;
    private final Map<String, String> data;
    private final PartnerEventIdResponse partnerId;
    private final String shareableId;
    private final String message;
    private final List<String> recipients;
    private final String recipientEmail;
    private final PublicPersonResponse friend;
    private final String subject;

    @JsonCreator
    public ShareResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @Nullable @JsonProperty(CHANNEL) String channel,
        @JsonProperty(SHARE_DATE) String shareDate,
        @JsonProperty(RECIPIENTS) List<String> recipients,
        @Nullable @JsonProperty(RECIPIENT_EMAIL) String recipientEmail,
        @JsonProperty(FRIEND) PublicPersonResponse friend,
        @Nullable @JsonProperty(LINK) String link,
        @Nullable @JsonProperty(PARTNER_ID) PartnerEventIdResponse partnerId,
        @JsonProperty(DATA) Map<String, String> data,
        @Nullable @JsonProperty(SHAREABLE_ID) String shareableId,
        @Nullable @JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(SUBJECT) String subject) {
        this.id = id;
        this.campaignId = campaignId;
        this.channel = channel;
        this.shareDate = shareDate;
        this.link = link;
        this.partnerId = partnerId;
        this.data = data != null ? Collections.unmodifiableMap(data) : Collections.emptyMap();
        this.shareableId = shareableId;
        this.message = message;
        this.recipients = recipients != null ? Collections.unmodifiableList(recipients) : Collections.emptyList();
        this.recipientEmail = recipientEmail;
        this.friend = friend;
        this.subject = subject;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @Deprecated // TODO remove ENG-12616
    @JsonProperty(SHARE_ID)
    public String getShareId() {
        return id;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @Nullable
    @JsonProperty(CHANNEL)
    public String getChannel() {
        return channel;
    }

    @JsonProperty(SHARE_DATE)
    public String getShareDate() {
        return shareDate;
    }

    @Nullable
    @JsonProperty(LINK)
    public String getLink() {
        return link;
    }

    @Nullable
    @JsonProperty(PARTNER_ID)
    public PartnerEventIdResponse getPartnerId() {
        return partnerId;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(RECIPIENTS)
    public List<String> getRecipients() {
        return recipients;
    }

    @Nullable
    @JsonProperty(RECIPIENT_EMAIL)
    public String getRecipientEmail() {
        return recipientEmail;
    }

    @JsonProperty(FRIEND)
    public PublicPersonResponse getFriend() {
        return friend;
    }

    @Nullable
    @JsonProperty(SUBJECT)
    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
