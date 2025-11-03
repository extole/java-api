package com.extole.client.rest.v0;

import static com.extole.client.rest.v0.ActionResponse.JSON_ACTION_DATE;
import static com.extole.client.rest.v0.ActionResponse.JSON_ACTION_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_ACTION_TYPE;
import static com.extole.client.rest.v0.ActionResponse.JSON_API_VERSION;
import static com.extole.client.rest.v0.ActionResponse.JSON_BROWSER_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_CAMPAIGN_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_CHANNEL;
import static com.extole.client.rest.v0.ActionResponse.JSON_CHANNEL_MESSAGE;
import static com.extole.client.rest.v0.ActionResponse.JSON_CLIENT_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_CLIENT_PARAMS;
import static com.extole.client.rest.v0.ActionResponse.JSON_CONTAINER;
import static com.extole.client.rest.v0.ActionResponse.JSON_CONVERSIONS_FLAG;
import static com.extole.client.rest.v0.ActionResponse.JSON_EMAIL;
import static com.extole.client.rest.v0.ActionResponse.JSON_FIRST_NAME;
import static com.extole.client.rest.v0.ActionResponse.JSON_HTTP_HEADERS;
import static com.extole.client.rest.v0.ActionResponse.JSON_LAST_NAME;
import static com.extole.client.rest.v0.ActionResponse.JSON_PARTNER_CONVERSION_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_PARTNER_USER_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_PERSON_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_PROGRAM_DOMAIN;
import static com.extole.client.rest.v0.ActionResponse.JSON_QUALITY_SCORE;
import static com.extole.client.rest.v0.ActionResponse.JSON_RECIPIENTS;
import static com.extole.client.rest.v0.ActionResponse.JSON_REVIEW_STATUS;
import static com.extole.client.rest.v0.ActionResponse.JSON_REWARDED;
import static com.extole.client.rest.v0.ActionResponse.JSON_SHAREABLE_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_SITE_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_SOURCE;
import static com.extole.client.rest.v0.ActionResponse.JSON_SOURCE_IP;
import static com.extole.client.rest.v0.ActionResponse.JSON_SOURCE_URL;
import static com.extole.client.rest.v0.ActionResponse.JSON_VIA_CLICK_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_VIA_SHARE_ID;
import static com.extole.client.rest.v0.ActionResponse.JSON_ZONE_NAME;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.common.lang.ToString;

@JsonPropertyOrder({JSON_REWARDED, JSON_CONVERSIONS_FLAG, JSON_ACTION_ID, JSON_CLIENT_ID, JSON_CAMPAIGN_ID,
    JSON_CHANNEL, JSON_ACTION_TYPE, JSON_ACTION_DATE, JSON_ZONE_NAME, JSON_SITE_ID, JSON_PROGRAM_DOMAIN,
    JSON_API_VERSION, JSON_SOURCE, JSON_EMAIL, JSON_FIRST_NAME, JSON_LAST_NAME, JSON_CHANNEL_MESSAGE, JSON_RECIPIENTS,
    JSON_SOURCE_URL, JSON_VIA_SHARE_ID, JSON_VIA_CLICK_ID, JSON_PERSON_ID, JSON_BROWSER_ID, JSON_SOURCE_IP,
    JSON_CLIENT_PARAMS, JSON_HTTP_HEADERS, JSON_PARTNER_USER_ID, JSON_PARTNER_CONVERSION_ID, JSON_QUALITY_SCORE,
    JSON_REVIEW_STATUS, JSON_SHAREABLE_ID, JSON_CONTAINER})
public final class ActionResponse {
    static final String JSON_REWARDED = "rewarded";
    static final String JSON_CONVERSIONS_FLAG = "conversions_flag";
    static final String JSON_ACTION_ID = "action_id";
    static final String JSON_CLIENT_ID = "client_id";
    static final String JSON_CAMPAIGN_ID = "campaign_id";
    static final String JSON_CHANNEL = "channel";
    static final String JSON_ACTION_TYPE = "action_type";
    static final String JSON_ACTION_DATE = "action_date";
    static final String JSON_ZONE_NAME = "zone_name";
    static final String JSON_SITE_ID = "site_id";
    static final String JSON_PROGRAM_DOMAIN = "program_domain";
    static final String JSON_API_VERSION = "api_version";
    static final String JSON_SOURCE = "source";
    static final String JSON_EMAIL = "email";
    static final String JSON_FIRST_NAME = "first_name";
    static final String JSON_LAST_NAME = "last_name";
    static final String JSON_CHANNEL_MESSAGE = "channel_message";
    static final String JSON_RECIPIENTS = "recipients";
    static final String JSON_SOURCE_URL = "source_url";
    static final String JSON_VIA_SHARE_ID = "via_share_id";
    static final String JSON_VIA_CLICK_ID = "via_click_id";
    static final String JSON_PERSON_ID = "person_id";
    static final String JSON_BROWSER_ID = "browser_id";
    static final String JSON_SOURCE_IP = "source_ip";
    static final String JSON_CLIENT_PARAMS = "client_params";
    static final String JSON_HTTP_HEADERS = "http_headers";
    static final String JSON_PARTNER_USER_ID = "partner_user_id";
    static final String JSON_PARTNER_CONVERSION_ID = "partner_conversion_id";
    static final String JSON_QUALITY_SCORE = "quality_score";
    static final String JSON_REVIEW_STATUS = "review_status";
    static final String JSON_SHAREABLE_ID = "shareable_id";
    private static final String JSON_AUTH_STATUS = "auth_status";
    static final String JSON_CONTAINER = "container";

    private final boolean rewarded;
    private final boolean conversionsFlag;
    private final String actionId;
    private final String clientId;
    private final String campaignId;
    private final String channel;
    private final ActionType actionType;
    private final ZonedDateTime actionDate;
    private final String zoneName;
    private final String siteId;
    private final String programDomain;
    private final String apiVersion;
    private final String source;
    private final String email;
    private final String channelMessage;
    private final List<String> recipients;
    private final String sourceUrl;
    private final Optional<String> viaShareId;
    private final Optional<String> viaClickId;
    private final String personId;
    private final Optional<String> browserId;
    private final String sourceIp;
    private final Map<String, String> clientParams;
    private final Map<String, List<String>> httpHeaders;
    private final String partnerUserId;
    private final String partnerConversionId;
    private final QualityScore qualityScore;
    private final ReviewStatus reviewStatus;
    private final Long shareableId;
    private final String container;

    @JsonCreator
    public ActionResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_CLIENT_ID) String clientId,
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CHANNEL) String channel,
        @JsonProperty(JSON_ACTION_TYPE) ActionType actionType,
        @JsonProperty(JSON_ACTION_DATE) ZonedDateTime actionDate,
        @JsonProperty(JSON_ZONE_NAME) String zoneName,
        @JsonProperty(JSON_SITE_ID) String siteId,
        @JsonProperty(JSON_PROGRAM_DOMAIN) String programDomain,
        @JsonProperty(JSON_API_VERSION) String apiVersion,
        @JsonProperty(JSON_SOURCE) String source,
        @JsonProperty(JSON_EMAIL) String email,
        @JsonProperty(JSON_CHANNEL_MESSAGE) String channelMessage,
        @JsonProperty(JSON_RECIPIENTS) List<String> recipients,
        @JsonProperty(JSON_SOURCE_URL) String sourceUrl,
        @JsonProperty(JSON_VIA_SHARE_ID) String viaShareId,
        @JsonProperty(JSON_VIA_CLICK_ID) String viaClickId,
        @JsonProperty(JSON_PERSON_ID) String personId,
        @JsonProperty(JSON_BROWSER_ID) String browserId,
        @JsonProperty(JSON_SOURCE_IP) String sourceIp,
        @JsonProperty(JSON_CLIENT_PARAMS) Map<String, String> clientParams,
        @JsonProperty(JSON_HTTP_HEADERS) Map<String, List<String>> httpHeaders,
        @JsonProperty(JSON_PARTNER_USER_ID) String partnerUserId,
        @JsonProperty(JSON_PARTNER_CONVERSION_ID) String partnerConversionId,
        @JsonProperty(JSON_QUALITY_SCORE) QualityScore qualityScore,
        @JsonProperty(JSON_REVIEW_STATUS) ReviewStatus reviewStatus,
        @JsonProperty(JSON_SHAREABLE_ID) Long shareableId,
        @JsonProperty(JSON_CONVERSIONS_FLAG) boolean conversionsFlag,
        @JsonProperty(JSON_REWARDED) boolean rewarded,
        @JsonProperty(JSON_CONTAINER) String container) {
        this.actionId = actionId;
        this.clientId = clientId;
        this.campaignId = campaignId;
        this.channel = channel;
        this.actionType = actionType;
        this.actionDate = actionDate;
        this.zoneName = zoneName;
        this.siteId = siteId;
        this.programDomain = programDomain;
        this.apiVersion = apiVersion;
        this.source = source;
        this.email = email;
        this.channelMessage = channelMessage;
        this.recipients = recipients;
        this.sourceUrl = sourceUrl;
        this.viaShareId = Optional.ofNullable(viaShareId);
        this.viaClickId = Optional.ofNullable(viaClickId);
        this.personId = personId;
        this.browserId = Optional.ofNullable(browserId);
        this.sourceIp = sourceIp;
        this.clientParams = clientParams;
        this.httpHeaders = httpHeaders;
        this.partnerUserId = partnerUserId;
        this.partnerConversionId = partnerConversionId;
        this.qualityScore = qualityScore;
        this.reviewStatus = reviewStatus;
        this.shareableId = shareableId;
        this.conversionsFlag = conversionsFlag;
        this.rewarded = rewarded;
        this.container = container;
    }

    @JsonProperty(JSON_REWARDED)
    public boolean getRewarded() {
        return rewarded;
    }

    @JsonProperty(JSON_CONVERSIONS_FLAG)
    public boolean getConversionsFlag() {
        return conversionsFlag;
    }

    @JsonProperty(JSON_ACTION_ID)
    public String getActionId() {
        return this.actionId;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return this.clientId;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_CHANNEL)
    public String getChannel() {
        return channel;
    }

    @JsonProperty(JSON_ACTION_TYPE)
    public ActionType getActionType() {
        return actionType;
    }

    @JsonProperty(JSON_ACTION_DATE)
    public ZonedDateTime getActionDate() {
        return actionDate;
    }

    @JsonProperty(JSON_ZONE_NAME)
    public String getZoneName() {
        return zoneName;
    }

    @JsonProperty(JSON_SITE_ID)
    public String getSiteId() {
        return siteId;
    }

    @JsonProperty(JSON_PROGRAM_DOMAIN)
    public String getProgramDomain() {
        return programDomain;
    }

    @JsonProperty(JSON_API_VERSION)
    public String getApiVersion() {
        return apiVersion;
    }

    @JsonProperty(JSON_SOURCE)
    public String getSource() {
        return source;
    }

    @JsonProperty(JSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_CHANNEL_MESSAGE)
    public String getChannelMessage() {
        return channelMessage;
    }

    @JsonProperty(JSON_RECIPIENTS)
    public List<String> getRecipients() {
        return recipients;
    }

    @JsonProperty(JSON_SOURCE_URL)
    public String getSourceUrl() {
        return sourceUrl;
    }

    @JsonProperty(JSON_VIA_SHARE_ID)
    public String getViaShareId() {
        return this.viaShareId.orElse(null);
    }

    @JsonProperty(JSON_VIA_CLICK_ID)
    public String getViaClickId() {
        return this.viaClickId.orElse(null);
    }

    @JsonProperty(JSON_PERSON_ID)
    public String getPersonId() {
        return this.personId;
    }

    @JsonProperty(JSON_AUTH_STATUS)
    public String getAuthStatus() {
        return "";
    }

    @JsonProperty(JSON_BROWSER_ID)
    public String getBrowserId() {
        return this.browserId.orElse(null);
    }

    @JsonProperty(JSON_SOURCE_IP)
    public String getSourceIp() {
        return sourceIp;
    }

    @JsonProperty(JSON_CLIENT_PARAMS)
    public Map<String, String> getClientParams() {
        return clientParams;
    }

    @JsonProperty(JSON_HTTP_HEADERS)
    public Map<String, List<String>> getHttpHeaders() {
        return httpHeaders;
    }

    @JsonProperty(JSON_PARTNER_USER_ID)
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @JsonProperty(JSON_PARTNER_CONVERSION_ID)
    public String getPartnerConversionId() {
        return partnerConversionId;
    }

    @JsonProperty(JSON_QUALITY_SCORE)
    public QualityScore getQualityScore() {
        return qualityScore;
    }

    @JsonProperty(JSON_REVIEW_STATUS)
    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    @JsonProperty(JSON_SHAREABLE_ID)
    public Long getShareableId() {
        return shareableId;
    }

    @JsonProperty(JSON_CONTAINER)
    public String getContainer() {
        return container;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(actionId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionResponse other = (ActionResponse) obj;
        if (!actionId.equals(other.actionId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
