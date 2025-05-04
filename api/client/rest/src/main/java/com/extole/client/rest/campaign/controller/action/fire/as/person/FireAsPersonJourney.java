package com.extole.client.rest.campaign.controller.action.fire.as.person;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.client.rest.person.PersonReferralReason;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class FireAsPersonJourney {

    private static final String JSON_JOURNEY_TYPE = "journey_type";
    private static final String JSON_REFERRAL_REASON = "referral_reason";
    private static final String JSON_COUPON_CODE = "coupon_code";
    private static final String JSON_ADVOCATE_CODE = "advocate_code";
    private static final String JSON_SHARE_ID = "share_id";
    private static final String JSON_SHAREABLE_ID = "shareable_id";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_CONTAINER = "container";
    private static final String JSON_LABEL = "label";
    private static final String JSON_REASON = "reason";
    private static final String JSON_ZONE = "zone";
    private static final String JSON_PROMOTABLE_CODE = "promotable_code";
    private static final String JSON_ADVOCATE_PARTNER_USER_ID = "advocate_partner_user_id";

    private final Optional<String> journeyName;
    private final Optional<PersonReferralReason> referralReason;
    private final Optional<String> couponCode;
    private final Optional<String> advocateCode;
    private final Optional<String> shareId;
    private final Optional<String> shareableId;
    private final Optional<String> campaignId;
    private final Optional<String> container;
    private final Optional<String> label;
    private final Optional<String> reason;
    private final Optional<String> zone;
    private final Optional<String> promotableCode;
    private final Optional<String> advocatePartnerUserId;

    @JsonCreator
    private FireAsPersonJourney(
        @JsonProperty(JSON_JOURNEY_TYPE) Optional<String> journeyName,
        @JsonProperty(JSON_REFERRAL_REASON) Optional<PersonReferralReason> referralReason,
        @JsonProperty(JSON_COUPON_CODE) Optional<String> couponCode,
        @JsonProperty(JSON_ADVOCATE_CODE) Optional<String> advocateCode,
        @JsonProperty(JSON_SHARE_ID) Optional<String> shareId,
        @JsonProperty(JSON_SHAREABLE_ID) Optional<String> shareableId,
        @JsonProperty(JSON_CAMPAIGN_ID) Optional<String> campaignId,
        @JsonProperty(JSON_CONTAINER) Optional<String> container,
        @JsonProperty(JSON_LABEL) Optional<String> label,
        @JsonProperty(JSON_REASON) Optional<String> reason,
        @JsonProperty(JSON_ZONE) Optional<String> zone,
        @JsonProperty(JSON_PROMOTABLE_CODE) Optional<String> promotableCode,
        @JsonProperty(JSON_ADVOCATE_PARTNER_USER_ID) Optional<String> advocatePartnerUserId) {
        this.journeyName = journeyName;
        this.referralReason = referralReason;
        this.couponCode = couponCode;
        this.advocateCode = advocateCode;
        this.shareId = shareId;
        this.shareableId = shareableId;
        this.campaignId = campaignId;
        this.container = container;
        this.label = label;
        this.reason = reason;
        this.zone = zone;
        this.promotableCode = promotableCode;
        this.advocatePartnerUserId = advocatePartnerUserId;
    }

    private FireAsPersonJourney(Builder builder) {
        this.journeyName = builder.journeyName;
        this.referralReason = builder.referralReason;
        this.couponCode = builder.couponCode;
        this.advocateCode = builder.advocateCode;
        this.shareId = builder.shareId;
        this.shareableId = builder.shareableId;
        this.campaignId = builder.campaignId;
        this.container = builder.container;
        this.label = builder.label;
        this.reason = builder.reason;
        this.zone = builder.zone;
        this.promotableCode = builder.promotableCode;
        this.advocatePartnerUserId = builder.advocatePartnerUserId;
    }

    @JsonProperty(JSON_JOURNEY_TYPE)
    public Optional<String> getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JSON_REFERRAL_REASON)
    public Optional<PersonReferralReason> getReferralReason() {
        return referralReason;
    }

    @JsonProperty(JSON_COUPON_CODE)
    public Optional<String> getCouponCode() {
        return couponCode;
    }

    @JsonProperty(JSON_ADVOCATE_CODE)
    public Optional<String> getAdvocateCode() {
        return advocateCode;
    }

    @JsonProperty(JSON_SHARE_ID)
    public Optional<String> getShareId() {
        return shareId;
    }

    @JsonProperty(JSON_SHAREABLE_ID)
    public Optional<String> getShareableId() {
        return shareableId;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public Optional<String> getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_CONTAINER)
    public Optional<String> getContainer() {
        return container;
    }

    @JsonProperty(JSON_LABEL)
    public Optional<String> getLabel() {
        return label;
    }

    @JsonProperty(JSON_REASON)
    public Optional<String> getReason() {
        return reason;
    }

    @JsonProperty(JSON_ZONE)
    public Optional<String> getZone() {
        return zone;
    }

    @JsonProperty(JSON_PROMOTABLE_CODE)
    public Optional<String> getPromotableCode() {
        return promotableCode;
    }

    @JsonProperty(JSON_ADVOCATE_PARTNER_USER_ID)
    public Optional<String> getAdvocatePartnerUserId() {
        return advocatePartnerUserId;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null) {
            return false;
        }

        if (this == otherObject) {
            return true;
        }

        if (getClass() != otherObject.getClass()) {
            return false;
        }

        FireAsPersonJourney otherJourney = (FireAsPersonJourney) otherObject;

        return EqualsBuilder.reflectionEquals(this, otherJourney);
    }

    public static class Builder {

        private Optional<String> journeyName = Optional.empty();
        private Optional<PersonReferralReason> referralReason = Optional.empty();
        private Optional<String> couponCode = Optional.empty();
        private Optional<String> advocateCode = Optional.empty();
        private Optional<String> shareId = Optional.empty();
        private Optional<String> shareableId = Optional.empty();
        private Optional<String> campaignId = Optional.empty();
        private Optional<String> container = Optional.empty();
        private Optional<String> label = Optional.empty();
        private Optional<String> reason = Optional.empty();
        private Optional<String> zone = Optional.empty();
        private Optional<String> promotableCode = Optional.empty();
        private Optional<String> advocatePartnerUserId = Optional.empty();

        public Builder withJourneyName(String journeyName) {
            this.journeyName = Optional.ofNullable(journeyName);
            return this;
        }

        public Builder withReferralReason(PersonReferralReason referralReason) {
            this.referralReason = Optional.ofNullable(referralReason);
            return this;
        }

        public Builder withCouponCode(String couponCode) {
            this.couponCode = Optional.ofNullable(couponCode);
            return this;
        }

        public Builder withAdvocateCode(String advocateCode) {
            this.advocateCode = Optional.ofNullable(advocateCode);
            return this;
        }

        public Builder withShareId(String shareId) {
            this.shareId = Optional.ofNullable(shareId);
            return this;
        }

        public Builder withShareableId(String shareableId) {
            this.shareableId = Optional.ofNullable(shareableId);
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = Optional.ofNullable(campaignId);
            return this;
        }

        public Builder withContainer(String container) {
            this.container = Optional.ofNullable(container);
            return this;
        }

        public Builder withLabel(String label) {
            this.label = Optional.ofNullable(label);
            return this;
        }

        public Builder withReason(String reason) {
            this.reason = Optional.ofNullable(reason);
            return this;
        }

        public Builder withZone(String zone) {
            this.zone = Optional.ofNullable(zone);
            return this;
        }

        public Builder withPromotableCode(String promotableCode) {
            this.promotableCode = Optional.ofNullable(promotableCode);
            return this;
        }

        public Builder withAdvocatePartnerUserId(String advocatePartnerUserId) {
            this.advocatePartnerUserId = Optional.ofNullable(advocatePartnerUserId);
            return this;
        }

        public FireAsPersonJourney build() {
            return new FireAsPersonJourney(this);
        }

    }

}
