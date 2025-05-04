package com.extole.consumer.rest.share.event;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove ENG-10140
public class ConsumerEventRequest {
    private static final String ORIGIN_EVENT_ID_PARAM = "origin_event_id";
    private static final String CAMPAIGN_ID_PARAM = "campaign_id";
    private static final String SOURCE_PARAM = "source";
    private static final String SOURCE_URL_PARAM = "source_url";
    private static final String ZONE_NAME_PARAM = "zone_name";
    private static final String PRODUCT_ID_PARAM = "product_id";
    private static final String SITE_HOST_NAME_PARAM = "site_host_id";
    private static final String CHANNEL_NAME_PARAM = "channel";
    private static final String VIA_ZONE_NAME_PARAM = "via_zone";
    private static final String PARAMETERS_PARAM = "parameters";
    private static final String RECEPIENT_EMAILS_PARAM = "recipient_emails";

    private final Long originEventId;
    private final String campaignId;
    private final String source;
    private final String sourceUrl;
    private final String zoneName;
    private final String productId;
    private final String siteHostName;
    private final String channelName;
    private final String viaZoneName;
    private final List<String> recipientEmails;
    private final Map<String, String> parameters;

    public ConsumerEventRequest(@JsonProperty(value = ORIGIN_EVENT_ID_PARAM, required = false) Long originEventId,
        @JsonProperty(value = CAMPAIGN_ID_PARAM, required = false) String campaignId,
        @JsonProperty(value = SOURCE_PARAM, required = false) String source,
        @JsonProperty(value = SOURCE_URL_PARAM, required = false) String sourceUrl,
        @JsonProperty(value = ZONE_NAME_PARAM, required = false) String zoneName,
        @JsonProperty(value = PRODUCT_ID_PARAM, required = false) String productId,
        @JsonProperty(value = RECEPIENT_EMAILS_PARAM, required = false) List<String> recipientEmails,
        @JsonProperty(value = SITE_HOST_NAME_PARAM, required = false) String siteHostName,
        @JsonProperty(value = CHANNEL_NAME_PARAM, required = true) String channelName,
        @JsonProperty(value = VIA_ZONE_NAME_PARAM, required = false) String viaZone,
        @JsonProperty(value = PARAMETERS_PARAM, required = false) Map<String, String> parameters) {
        this.originEventId = originEventId;
        this.campaignId = campaignId;
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.zoneName = zoneName;
        this.productId = productId;
        this.recipientEmails = recipientEmails;
        this.siteHostName = siteHostName;
        this.channelName = channelName;
        this.viaZoneName = viaZone;
        this.parameters = parameters;
    }

    @Nullable
    @JsonProperty(value = ORIGIN_EVENT_ID_PARAM, required = false)
    public Long getOriginEventId() {
        return originEventId;
    }

    @Nullable
    @JsonProperty(value = CAMPAIGN_ID_PARAM, required = false)
    public String getCampaignId() {
        return campaignId;
    }

    @Nullable
    @JsonProperty(value = SOURCE_PARAM, required = false)
    public String getSource() {
        return source;
    }

    @Nullable
    @JsonProperty(value = SOURCE_URL_PARAM, required = false)
    public String getSourceUrl() {
        return sourceUrl;
    }

    @Nullable
    @JsonProperty(value = RECEPIENT_EMAILS_PARAM, required = false)
    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    @Nullable
    @JsonProperty(value = ZONE_NAME_PARAM, required = false)
    public String getZoneName() {
        return zoneName;
    }

    @Nullable
    @JsonProperty(value = PRODUCT_ID_PARAM, required = false)
    public String getProductId() {
        return productId;
    }

    @Nullable
    @JsonProperty(value = SITE_HOST_NAME_PARAM, required = false)
    public String getSiteHostName() {
        return siteHostName;
    }

    @Nullable
    @JsonProperty(value = CHANNEL_NAME_PARAM, required = true)
    public String getChannelName() {
        return channelName;
    }

    @Nullable
    @JsonProperty(value = VIA_ZONE_NAME_PARAM, required = false)
    public String getViaZoneName() {
        return viaZoneName;
    }

    @Nullable
    @JsonProperty(value = PARAMETERS_PARAM, required = false)
    public Map<String, String> getParameters() {
        return parameters;
    }
}
