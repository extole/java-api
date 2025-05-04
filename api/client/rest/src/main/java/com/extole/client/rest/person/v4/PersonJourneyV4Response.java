package com.extole.client.rest.person.v4;

import java.time.ZonedDateTime;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public class PersonJourneyV4Response {

    private static final String ID = "id";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String ENTRY_LABEL = "entry_label";
    private static final String CONTAINER = "container";
    // TODO Rename to journey_name - ENG-18547
    private static final String JOURNEY_TYPE = "journey_type";
    private static final String ENTRY_REASON = "entry_reason";
    private static final String ENTRY_ZONE = "entry_zone";
    private static final String LAST_ZONE = "last_zone";
    private static final String ENTRY_SHARE_ID = "entry_share_id";
    private static final String LAST_SHARE_ID = "last_share_id";
    private static final String ENTRY_SHAREABLE_ID = "entry_shareable_id";
    private static final String LAST_SHAREABLE_ID = "last_shareable_id";
    private static final String ENTRY_ADVOCATE_CODE = "entry_advocate_code";
    private static final String LAST_ADVOCATE_CODE = "last_advocate_code";
    private static final String ENTRY_PROMOTABLE_CODE = "entry_promotable_code";
    private static final String LAST_PROMOTABLE_CODE = "last_promotable_code";
    private static final String ENTRY_CONSUMER_EVENT_ID = "entry_consumer_event_id";
    private static final String LAST_CONSUMER_EVENT_ID = "last_consumer_event_id";
    private static final String ENTRY_PROFILE_ID = "entry_profile_id";
    private static final String LAST_PROFILE_ID = "last_profile_id";
    private static final String ENTRY_ADVOCATE_PARTNER_ID = "entry_advocate_partner_id";
    private static final String LAST_ADVOCATE_PARTNER_ID = "last_advocate_partner_id";
    private static final String ENTRY_COUPON_CODE = "entry_coupon_code";
    private static final String LAST_COUPON_CODE = "last_coupon_code";
    private static final String ENTRY_REFERRAL_REASON = "entry_referral_reason";
    private static final String LAST_REFERRAL_REASON = "last_referral_reason";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String DATA = "data";

    private final String id;
    private final String campaignId;
    private final String entryLabel;
    private final String container;
    private final String journeyName;
    private final String entryReason;
    private final String entryZone;
    private final String lastZone;
    private final String entryShareId;
    private final String lastShareId;
    private final String entryShareableId;
    private final String lastShareableId;
    private final String entryAdvocateCode;
    private final String lastAdvocateCode;
    private final String entryPromotableCode;
    private final String lastPromotableCode;
    private final String entryConsumerEventId;
    private final String lastConsumerEventId;
    private final String entryProfileId;
    private final String lastProfileId;
    private final String entryAdvocatePartnerId;
    private final String lastAdvocatePartnerId;
    private final String entryCouponCode;
    private final String lastCouponCode;
    private final String entryReferralReason;
    private final String lastReferralReason;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final List<PersonDataV4Response> data;

    public PersonJourneyV4Response(
        @JsonProperty(ID) String id,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(ENTRY_LABEL) String entryLabel,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(JOURNEY_TYPE) String journeyName,
        @JsonProperty(ENTRY_REASON) String entryReason,
        @JsonProperty(ENTRY_ZONE) String entryZone,
        @JsonProperty(LAST_ZONE) String lastZone,
        @JsonProperty(ENTRY_SHARE_ID) String entryShareId,
        @JsonProperty(LAST_SHARE_ID) String lastShareId,
        @JsonProperty(ENTRY_SHAREABLE_ID) String entryShareableId,
        @JsonProperty(LAST_SHAREABLE_ID) String lastShareableId,
        @JsonProperty(ENTRY_ADVOCATE_CODE) String entryAdvocateCode,
        @JsonProperty(LAST_ADVOCATE_CODE) String lastAdvocateCode,
        @JsonProperty(ENTRY_PROMOTABLE_CODE) String entryPromotableCode,
        @JsonProperty(LAST_PROMOTABLE_CODE) String lastPromotableCode,
        @JsonProperty(ENTRY_CONSUMER_EVENT_ID) String entryConsumerEventId,
        @JsonProperty(LAST_CONSUMER_EVENT_ID) String lastConsumerEventId,
        @JsonProperty(ENTRY_PROFILE_ID) String entryProfileId,
        @JsonProperty(LAST_PROFILE_ID) String lastProfileId,
        @JsonProperty(ENTRY_ADVOCATE_PARTNER_ID) String entryAdvocatePartnerId,
        @JsonProperty(LAST_ADVOCATE_PARTNER_ID) String lastAdvocatePartnerId,
        @JsonProperty(ENTRY_COUPON_CODE) String entryCouponCode,
        @JsonProperty(LAST_COUPON_CODE) String lastCouponCode,
        @JsonProperty(ENTRY_REFERRAL_REASON) String entryReferralReason,
        @JsonProperty(LAST_REFERRAL_REASON) String lastReferralReason,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(DATA) List<PersonDataV4Response> data) {

        this.id = id;
        this.campaignId = campaignId;
        this.entryLabel = entryLabel;
        this.container = container;
        this.journeyName = journeyName;
        this.entryReason = entryReason;
        this.entryZone = entryZone;
        this.lastZone = lastZone;
        this.entryShareId = entryShareId;
        this.lastShareId = lastShareId;
        this.entryShareableId = entryShareableId;
        this.lastShareableId = lastShareableId;
        this.entryAdvocateCode = entryAdvocateCode;
        this.lastAdvocateCode = lastAdvocateCode;
        this.entryPromotableCode = entryPromotableCode;
        this.lastPromotableCode = lastPromotableCode;
        this.entryConsumerEventId = entryConsumerEventId;
        this.lastConsumerEventId = lastConsumerEventId;
        this.entryProfileId = entryProfileId;
        this.lastProfileId = lastProfileId;
        this.entryAdvocatePartnerId = entryAdvocatePartnerId;
        this.lastAdvocatePartnerId = lastAdvocatePartnerId;
        this.entryCouponCode = entryCouponCode;
        this.lastCouponCode = lastCouponCode;
        this.entryReferralReason = entryReferralReason;
        this.lastReferralReason = lastReferralReason;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(ENTRY_LABEL)
    public String getEntryLabel() {
        return entryLabel;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(JOURNEY_TYPE)
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(ENTRY_REASON)
    public String getEntryReason() {
        return entryReason;
    }

    @JsonProperty(ENTRY_ZONE)
    public String getEntryZone() {
        return entryZone;
    }

    @JsonProperty(LAST_ZONE)
    public String getLastZone() {
        return lastZone;
    }

    @Nullable
    @JsonProperty(ENTRY_SHARE_ID)
    public String getEntryShareId() {
        return entryShareId;
    }

    @Nullable
    @JsonProperty(LAST_SHARE_ID)
    public String getLastShareId() {
        return lastShareId;
    }

    @Nullable
    @JsonProperty(ENTRY_SHAREABLE_ID)
    public String getEntryShareableId() {
        return entryShareableId;
    }

    @Nullable
    @JsonProperty(LAST_SHAREABLE_ID)
    public String getLastShareableId() {
        return lastShareableId;
    }

    @Nullable
    @JsonProperty(ENTRY_ADVOCATE_CODE)
    public String getEntryAdvocateCode() {
        return entryAdvocateCode;
    }

    @Nullable
    @JsonProperty(LAST_ADVOCATE_CODE)
    public String getLastAdvocateCode() {
        return lastAdvocateCode;
    }

    @Nullable
    @JsonProperty(ENTRY_PROMOTABLE_CODE)
    public String getEntryPromotableCode() {
        return entryPromotableCode;
    }

    @Nullable
    @JsonProperty(LAST_PROMOTABLE_CODE)
    public String getLastPromotableCode() {
        return lastPromotableCode;
    }

    @Nullable
    @JsonProperty(ENTRY_CONSUMER_EVENT_ID)
    public String getEntryConsumerEventId() {
        return entryConsumerEventId;
    }

    @Nullable
    @JsonProperty(LAST_CONSUMER_EVENT_ID)
    public String getLastConsumerEventId() {
        return lastConsumerEventId;
    }

    @Nullable
    @JsonProperty(ENTRY_PROFILE_ID)
    public String getEntryProfileId() {
        return entryProfileId;
    }

    @Nullable
    @JsonProperty(LAST_PROFILE_ID)
    public String getLastProfileId() {
        return lastProfileId;
    }

    @Nullable
    @JsonProperty(ENTRY_ADVOCATE_PARTNER_ID)
    public String getEntryAdvocatePartnerId() {
        return entryAdvocatePartnerId;
    }

    @Nullable
    @JsonProperty(LAST_ADVOCATE_PARTNER_ID)
    public String getLastAdvocatePartnerId() {
        return lastAdvocatePartnerId;
    }

    @Nullable
    @JsonProperty(ENTRY_COUPON_CODE)
    public String getEntryCouponCode() {
        return entryCouponCode;
    }

    @Nullable
    @JsonProperty(LAST_COUPON_CODE)
    public String getLastCouponCode() {
        return lastCouponCode;
    }

    @Nullable
    @JsonProperty(ENTRY_REFERRAL_REASON)
    public String getEntryReferralReason() {
        return entryReferralReason;
    }

    @Nullable
    @JsonProperty(LAST_REFERRAL_REASON)
    public String getLastReferralReason() {
        return lastReferralReason;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(DATA)
    public List<PersonDataV4Response> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
