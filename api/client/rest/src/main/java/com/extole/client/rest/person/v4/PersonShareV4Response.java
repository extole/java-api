package com.extole.client.rest.person.v4;

import java.time.ZonedDateTime;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.common.lang.ToString;

public class PersonShareV4Response {

    private static final String SHARE_ID = "share_id";
    private static final String SHAREABLE_ID = "shareable_id";
    private static final String CHANNEL = "channel";
    private static final String MESSAGE = "message";
    private static final String SHARE_DATE = "share_date";
    private static final String RECIPIENT = "recipient";
    private static final String FRIEND_PERSON_ID = "friend_person_id";
    private static final String LINK = "link";
    private static final String DATA = "data";
    private static final String PARTNER_ID = "partner_id";
    private static final String SUBJECT = "subject";

    private final String shareId;
    private final String shareableId;
    private final String channel;
    private final String message;
    private final ZonedDateTime shareDate;
    private final String recipient;
    private final String friendPersonId;
    private final String link;
    private final Map<String, String> data;
    private final PartnerEventIdResponse partnerId;
    private final String subject;

    @JsonCreator
    public PersonShareV4Response(
        @JsonProperty(SHARE_ID) String shareId,
        @JsonProperty(SHAREABLE_ID) String shareableId,
        @Nullable @JsonProperty(CHANNEL) String channel,
        @Nullable @JsonProperty(MESSAGE) String message,
        @JsonProperty(SHARE_DATE) ZonedDateTime shareDate,
        @Nullable @JsonProperty(RECIPIENT) String recipient,
        @Nullable @JsonProperty(FRIEND_PERSON_ID) String friendPersonId,
        @JsonProperty(LINK) String link,
        @JsonProperty(DATA) Map<String, String> data,
        @Nullable @JsonProperty(PARTNER_ID) PartnerEventIdResponse partnerId,
        @Nullable @JsonProperty(SUBJECT) String subject) {
        this.shareId = shareId;
        this.shareableId = shareableId;
        this.channel = channel;
        this.message = message;
        this.shareDate = shareDate;
        this.recipient = recipient;
        this.friendPersonId = friendPersonId;
        this.link = link;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
        this.partnerId = partnerId;
        this.subject = subject;
    }

    @JsonProperty(SHARE_ID)
    public String getShareId() {
        return shareId;
    }

    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @Nullable
    @JsonProperty(CHANNEL)
    public String getChannel() {
        return channel;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(SHARE_DATE)
    public ZonedDateTime getShareDate() {
        return shareDate;
    }

    @Nullable
    @JsonProperty(RECIPIENT)
    public String getRecipient() {
        return recipient;
    }

    @Nullable
    @JsonProperty(FRIEND_PERSON_ID)
    public String getFriendPersonId() {
        return friendPersonId;
    }

    @JsonProperty(LINK)
    public String getLink() {
        return link;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(PARTNER_ID)
    public PartnerEventIdResponse getPartnerId() {
        return partnerId;
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
