package com.extole.client.rest.person.v2;

import java.time.ZonedDateTime;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.common.lang.ToString;

public class ShareV2Response {

    private static final String CHANNEL = "channel";
    private static final String SHARE_DATE = "share_date";
    private static final String RECIPIENT = "recipient";
    private static final String RECIPIENTS = "recipients";
    private static final String FRIEND = "friend";
    private static final String LINK = "link";
    private static final String PARTNER_ID = "partner_id";
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";

    private final String channel;
    private final ZonedDateTime shareDate;
    private final String recipient;
    private final PersonV2Response friend;
    private final String link;
    private final PartnerEventIdV2Response partnerId;
    private final String message;
    private final String subject;

    @JsonCreator
    public ShareV2Response(
        @JsonProperty(CHANNEL) String channel,
        @JsonProperty(SHARE_DATE) ZonedDateTime shareDate,
        @JsonProperty(RECIPIENT) String recipient,
        @JsonProperty(FRIEND) PersonV2Response friend,
        @JsonProperty(LINK) String link,
        @Nullable @JsonProperty(PARTNER_ID) PartnerEventIdV2Response partnerId,
        @Nullable @JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(SUBJECT) String subject) {
        this.channel = channel;
        this.shareDate = shareDate;
        this.recipient = recipient;
        this.friend = friend;
        this.link = link;
        this.partnerId = partnerId;
        this.message = message;
        this.subject = subject;
    }

    @JsonProperty(CHANNEL)
    public String getChannel() {
        return channel;
    }

    @JsonProperty(SHARE_DATE)
    public ZonedDateTime getDateIssued() {
        return shareDate;
    }

    @JsonProperty(RECIPIENT)
    public String getRecipient() {
        return recipient;
    }

    @Deprecated // TBD - OPEN TICKET use getRecipient
    @JsonProperty(RECIPIENTS)
    public List<String> getRecipients() {
        return recipient != null ? Lists.newArrayList(recipient) : Lists.newArrayList();
    }

    @JsonProperty(FRIEND)
    public PersonV2Response getFriend() {
        return friend;
    }

    @JsonProperty(LINK)
    public String getLink() {
        return link;
    }

    @Nullable
    @JsonProperty(PARTNER_ID)
    public PartnerEventIdV2Response getPartnerId() {
        return partnerId;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
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
