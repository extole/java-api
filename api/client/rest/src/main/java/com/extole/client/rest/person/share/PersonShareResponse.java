package com.extole.client.rest.person.share;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.common.lang.ToString;

public class PersonShareResponse {

    private static final String ID = "id";
    private static final String PROGRAM = "program";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String SHAREABLE_CODE = "shareable_code";
    private static final String LINK = "link";
    private static final String CHANNEL = "channel";
    private static final String MESSAGE = "message";
    private static final String OTHER_PERSON_ID = "other_person_id";
    private static final String PARTNER_ID = "partner_id";
    private static final String SUBJECT = "subject";
    private static final String DATA = "data";
    private static final String CREATED_DATE = "created_date";

    private final String id;
    private final String program;
    private final String campaignId;
    private final String shareableCode;
    private final String link;
    private final Optional<String> channel;
    private final Optional<String> message;
    private final Optional<String> otherPersonId;
    private final Optional<PartnerEventIdResponse> partnerId;
    private final Optional<String> subject;
    private final Map<String, PersonShareDataResponse> data;
    private final ZonedDateTime createdDate;

    @JsonCreator
    public PersonShareResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(PROGRAM) String program,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(SHAREABLE_CODE) String shareableCode,
        @JsonProperty(LINK) String link,
        @JsonProperty(CHANNEL) Optional<String> channel,
        @JsonProperty(MESSAGE) Optional<String> message,
        @JsonProperty(OTHER_PERSON_ID) Optional<String> otherPersonId,
        @JsonProperty(PARTNER_ID) Optional<PartnerEventIdResponse> partnerId,
        @JsonProperty(SUBJECT) Optional<String> subject,
        @JsonProperty(DATA) Map<String, PersonShareDataResponse> data,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate) {
        this.id = id;
        this.program = program;
        this.campaignId = campaignId;
        this.shareableCode = shareableCode;
        this.link = link;
        this.channel = channel;
        this.message = message;
        this.otherPersonId = otherPersonId;
        this.partnerId = partnerId;
        this.subject = subject;
        this.data = data == null ? ImmutableMap.of() : ImmutableMap.copyOf(data);
        this.createdDate = createdDate;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(PROGRAM)
    public String getProgram() {
        return program;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(SHAREABLE_CODE)
    public String getShareableCode() {
        return shareableCode;
    }

    @JsonProperty(LINK)
    public String getLink() {
        return link;
    }

    @JsonProperty(CHANNEL)
    public Optional<String> getChannel() {
        return channel;
    }

    @JsonProperty(MESSAGE)
    public Optional<String> getMessage() {
        return message;
    }

    @JsonProperty(OTHER_PERSON_ID)
    public Optional<String> getOtherPersonId() {
        return otherPersonId;
    }

    @JsonProperty(PARTNER_ID)
    public Optional<PartnerEventIdResponse> getPartnerId() {
        return partnerId;
    }

    @JsonProperty(SUBJECT)
    public Optional<String> getSubject() {
        return subject;
    }

    @JsonProperty(DATA)
    public Map<String, PersonShareDataResponse> getData() {
        return data;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new PersonShareResponse.Builder();
    }

    public static final class Builder {
        private String id;
        private String program;
        private String campaignId;
        private String shareableCode;
        private String link;
        private Optional<String> channel = Optional.empty();
        private Optional<String> message = Optional.empty();
        private Optional<String> otherPersonId = Optional.empty();
        private Optional<PartnerEventIdResponse> partnerId = Optional.empty();
        private Optional<String> subject = Optional.empty();
        private Map<String, PersonShareDataResponse> data;
        private ZonedDateTime createdDate;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withProgram(String program) {
            this.program = program;
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withShareableCode(String shareableCode) {
            this.shareableCode = shareableCode;
            return this;
        }

        public Builder withLink(String link) {
            this.link = link;
            return this;
        }

        public Builder withChannel(String channel) {
            this.channel = Optional.ofNullable(channel);
            return this;
        }

        public Builder withMessage(String message) {
            this.message = Optional.ofNullable(message);
            return this;
        }

        public Builder withOtherPersonId(String otherPersonId) {
            this.otherPersonId = Optional.ofNullable(otherPersonId);
            return this;
        }

        public Builder withPartnerId(PartnerEventIdResponse partnerId) {
            this.partnerId = Optional.ofNullable(partnerId);
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = Optional.ofNullable(subject);
            return this;
        }

        public Builder withData(Map<String, PersonShareDataResponse> data) {
            this.data = data;
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public PersonShareResponse build() {
            return new PersonShareResponse(id, program, campaignId, shareableCode, link, channel, message,
                otherPersonId, partnerId, subject, data, createdDate);
        }
    }
}
