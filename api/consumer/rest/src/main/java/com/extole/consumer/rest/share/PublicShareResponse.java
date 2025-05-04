package com.extole.consumer.rest.share;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.me.PartnerEventIdResponse;

public class PublicShareResponse {

    protected static final String ID = "id";
    protected static final String SHARE_ID = "share_id";
    protected static final String CAMPAIGN_ID = "campaign_id";
    protected static final String CHANNEL = "channel";
    protected static final String SHARE_DATE = "share_date";
    protected static final String LINK = "link";
    protected static final String PARTNER_ID = "partner_id";
    protected static final String SHAREABLE_ID = "shareable_id";

    private final String id;
    private final String campaignId;
    private final String channel;
    private final String shareDate;
    private final String link;
    private final PartnerEventIdResponse partnerId;
    private final String shareableId;

    public PublicShareResponse(@JsonProperty(ID) String id,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @Nullable @JsonProperty(CHANNEL) String channel,
        @JsonProperty(SHARE_DATE) String shareDate,
        @Nullable @JsonProperty(LINK) String link,
        @Nullable @JsonProperty(PARTNER_ID) PartnerEventIdResponse partnerId,
        @Nullable @JsonProperty(SHAREABLE_ID) String shareableId) {
        this.id = id;
        this.campaignId = campaignId;
        this.channel = channel;
        this.shareDate = shareDate;
        this.link = link;
        this.partnerId = partnerId;
        this.shareableId = shareableId;
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

    @Nullable
    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
