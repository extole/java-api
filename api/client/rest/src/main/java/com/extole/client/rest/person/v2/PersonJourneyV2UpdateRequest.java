package com.extole.client.rest.person.v2;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.data.PersonDataRequest;

public class PersonJourneyV2UpdateRequest {
    private static final String LABEL = "label";
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
    private static final String CONTAINER = "container";
    private static final String DATA = "data";

    private final String label;
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
    private final String container;
    private final List<PersonDataRequest> data;

    public PersonJourneyV2UpdateRequest(
        @Nullable @JsonProperty(LABEL) String label,
        @Nullable @JsonProperty(REASON) String reason,
        @Nullable @JsonProperty(ZONE) String zone,
        @Nullable @JsonProperty(SHARE_ID) String shareId,
        @Nullable @JsonProperty(SHAREABLE_ID) String shareableId,
        @Nullable @JsonProperty(ADVOCATE_CODE) String advocateCode,
        @Nullable @JsonProperty(PROMOTABLE_CODE) String promotableCode,
        @Nullable @JsonProperty(CONSUMER_EVENT_ID) String consumerEventId,
        @Nullable @JsonProperty(ADVOCATE_PARTNER_ID) String advocatePartnerId,
        @Nullable @JsonProperty(COUPON_CODE) String couponCode,
        @Nullable @JsonProperty(REFERRAL_REASON) String referralReason,
        @Nullable @JsonProperty(CONTAINER) String container,
        @Nullable @JsonProperty(DATA) List<PersonDataRequest> data) {
        this.label = label;
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
        this.container = container;
        this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
    }

    @Nullable
    @JsonProperty(LABEL)
    public String getLabel() {
        return label;
    }

    @Nullable
    @JsonProperty(REASON)
    public String getReason() {
        return reason;
    }

    @Nullable
    @JsonProperty(ZONE)
    public String getZone() {
        return zone;
    }

    @Nullable
    @JsonProperty(SHARE_ID)
    public String getShareId() {
        return shareId;
    }

    @Nullable
    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @Nullable
    @JsonProperty(ADVOCATE_CODE)
    public String getAdvocateCode() {
        return advocateCode;
    }

    @Nullable
    @JsonProperty(PROMOTABLE_CODE)
    public String getPromotableCode() {
        return promotableCode;
    }

    @Nullable
    @JsonProperty(CONSUMER_EVENT_ID)
    public String getConsumerEventId() {
        return consumerEventId;
    }

    @Nullable
    @JsonProperty(ADVOCATE_PARTNER_ID)
    public String getAdvocatePartnerId() {
        return advocatePartnerId;
    }

    @Nullable
    @JsonProperty(COUPON_CODE)
    public String getCouponCode() {
        return couponCode;
    }

    @Nullable
    @JsonProperty(REFERRAL_REASON)
    public String getReferralReason() {
        return referralReason;
    }

    @Nullable
    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(DATA)
    public List<PersonDataRequest> getData() {
        return data;
    }

    public static PersonJourneyUpdateRequestBuilder builder() {
        return new PersonJourneyUpdateRequestBuilder();
    }

    public static final class PersonJourneyUpdateRequestBuilder {

        private String label;
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
        private String container;
        private final List<PersonDataRequest> data = Lists.newArrayList();

        private PersonJourneyUpdateRequestBuilder() {
        }

        public PersonJourneyUpdateRequestBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withZone(String zone) {
            this.zone = zone;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withShareId(String shareId) {
            this.shareId = shareId;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withShareableId(String shareableId) {
            this.shareableId = shareableId;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withAdvocateCode(String advocateCode) {
            this.advocateCode = advocateCode;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withPromotableCode(String promotableCode) {
            this.promotableCode = promotableCode;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withConsumerEventId(String consumerEventId) {
            this.consumerEventId = consumerEventId;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withAdvocatePartnerId(String advocatePartnerId) {
            this.advocatePartnerId = advocatePartnerId;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withCouponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withReferralReason(String referralReason) {
            this.referralReason = referralReason;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder withContainer(String container) {
            this.container = container;
            return this;
        }

        public PersonJourneyUpdateRequestBuilder addData(String name, Object value, PersonDataScope scope) {
            this.data.add(new PersonDataRequest(name, value, scope));
            return this;
        }

        public PersonJourneyV2UpdateRequest build() {
            return new PersonJourneyV2UpdateRequest(label, reason, zone, shareId, shareableId, advocateCode,
                promotableCode, consumerEventId, advocatePartnerId, couponCode, referralReason, container, data);
        }
    }

}
