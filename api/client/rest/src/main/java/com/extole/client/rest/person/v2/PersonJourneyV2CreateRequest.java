package com.extole.client.rest.person.v2;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.data.PersonDataRequest;

public class PersonJourneyV2CreateRequest {

    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String LABEL = "label";
    private static final String CONTAINER = "container";
    // TODO Rename to journey_name - ENG-18547
    private static final String JOURNEY_TYPE = "journey_type";
    private static final String REASON = "reason";
    private static final String ZONE = "zone";
    private static final String SHARE_ID = "share_id";
    private static final String SHAREABLE_ID = "shareable_id";
    private static final String ADVOCATE_CODE = "advocate_code";
    private static final String PROMOTABLE_CODE = "promotable_code";
    private static final String CONSUMER_EVENT_ID = "consumer_event_id";
    private static final String ADVOCATE_PARTNER_ID = "advocate_partner_id";
    private static final String COUPON_CODE = "coupon_code";
    private static final String REFERRAL_REASON = "referral_reason";
    private static final String DATA = "data";
    private static final String KEY = "key";

    private final String campaignId;
    private final String label;
    private final String container;
    private final String journeyName;
    private final String reason;
    private final String zone;
    private final String shareId;
    private final String shareableId;
    private final String advocateCode;
    private final String promotableCode;
    private final String consumerEventId;
    private final String advocatePartnerId;
    private final String couponCode;
    private final String referralReason;
    private final List<PersonDataRequest> data;
    private final Optional<JourneyKey> key;

    public PersonJourneyV2CreateRequest(
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(LABEL) String label,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(JOURNEY_TYPE) String journeyName,
        @JsonProperty(REASON) String reason,
        @JsonProperty(ZONE) String zone,
        @JsonProperty(SHARE_ID) String shareId,
        @JsonProperty(SHAREABLE_ID) String shareableId,
        @JsonProperty(ADVOCATE_CODE) String advocateCode,
        @JsonProperty(PROMOTABLE_CODE) String promotableCode,
        @JsonProperty(CONSUMER_EVENT_ID) String consumerEventId,
        @JsonProperty(ADVOCATE_PARTNER_ID) String advocatePartnerId,
        @JsonProperty(COUPON_CODE) String couponCode,
        @JsonProperty(REFERRAL_REASON) String referralReason,
        @JsonProperty(DATA) List<PersonDataRequest> data,
        @JsonProperty(KEY) Optional<JourneyKey> key) {
        this.campaignId = campaignId;
        this.label = label;
        this.container = container;
        this.journeyName = journeyName;
        this.reason = reason;
        this.zone = zone;
        this.shareId = shareId;
        this.shareableId = shareableId;
        this.advocateCode = advocateCode;
        this.promotableCode = promotableCode;
        this.consumerEventId = consumerEventId;
        this.advocatePartnerId = advocatePartnerId;
        this.couponCode = couponCode;
        this.referralReason = referralReason;
        this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
        this.key = key;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(JOURNEY_TYPE)
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(REASON)
    public String getReason() {
        return reason;
    }

    @JsonProperty(ZONE)
    public String getZone() {
        return zone;
    }

    @JsonProperty(SHARE_ID)
    public String getShareId() {
        return shareId;
    }

    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @JsonProperty(ADVOCATE_CODE)
    public String getAdvocateCode() {
        return advocateCode;
    }

    @JsonProperty(PROMOTABLE_CODE)
    public String getPromotableCode() {
        return promotableCode;
    }

    @JsonProperty(CONSUMER_EVENT_ID)
    public String getConsumerEventId() {
        return consumerEventId;
    }

    @JsonProperty(ADVOCATE_PARTNER_ID)
    public String getAdvocatePartnerId() {
        return advocatePartnerId;
    }

    @JsonProperty(COUPON_CODE)
    public String getCouponCode() {
        return couponCode;
    }

    @JsonProperty(REFERRAL_REASON)
    public String getReferralReason() {
        return referralReason;
    }

    @JsonProperty(DATA)
    public List<PersonDataRequest> getData() {
        return data;
    }

    @JsonProperty(KEY)
    public Optional<JourneyKey> getKey() {
        return key;
    }

    public static PersonJourneyCreateRequestBuilder builder() {
        return new PersonJourneyCreateRequestBuilder();
    }

    public static final class PersonJourneyCreateRequestBuilder {

        private String campaignId;
        private String label;
        private String container;
        private String journeyName;
        private String reason;
        private String zone;
        private String shareId;
        private String shareableId;
        private String advocateCode;
        private String promotableCode;
        private String consumerEventId;
        private String advocatePartnerId;
        private String couponCode;
        private String referralReason;
        private final List<PersonDataRequest> data = Lists.newArrayList();
        private Optional<JourneyKey> key = Optional.empty();

        private PersonJourneyCreateRequestBuilder() {
        }

        public PersonJourneyCreateRequestBuilder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withContainer(String container) {
            this.container = container;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withJourneyName(String journeyName) {
            this.journeyName = journeyName;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withZone(String zone) {
            this.zone = zone;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withShareId(String shareId) {
            this.shareId = shareId;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withShareableId(String shareableId) {
            this.shareableId = shareableId;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withAdvocateCode(String advocateCode) {
            this.advocateCode = advocateCode;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withPromotableCode(String promotableCode) {
            this.promotableCode = promotableCode;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withConsumerEventId(String consumerEventId) {
            this.consumerEventId = consumerEventId;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withAdvocatePartnerId(String advocatePartnerId) {
            this.advocatePartnerId = advocatePartnerId;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withCouponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withReferralReason(String referralReason) {
            this.referralReason = referralReason;
            return this;
        }

        public PersonJourneyCreateRequestBuilder withKey(JourneyKey key) {
            this.key = Optional.of(key);
            return this;
        }

        public PersonJourneyCreateRequestBuilder addData(String name, Object value, PersonDataScope scope) {
            this.data.add(new PersonDataRequest(name, value, scope));
            return this;
        }

        public PersonJourneyV2CreateRequest build() {
            return new PersonJourneyV2CreateRequest(campaignId, label, container, journeyName, reason, zone, shareId,
                shareableId, advocateCode, promotableCode, consumerEventId, advocatePartnerId, couponCode,
                referralReason, data, key);
        }

    }

}
