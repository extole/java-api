package com.extole.client.rest.campaign.configuration;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionRedeemRewardConfiguration extends CampaignControllerActionConfiguration {

    private static final String REWARD_ID = "reward_id";
    private static final String DATA = "data";
    private static final String PARTNER_EVENT_ID = "partner_event_id";
    private static final String EVENT_TIME = "event_time";

    private final String rewardId;
    private final Map<String, String> data;
    private final Optional<String> partnerEventId;
    private final RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime;

    public CampaignControllerActionRedeemRewardConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(PARTNER_EVENT_ID) Optional<String> partnerEventId,
        @JsonProperty(EVENT_TIME) RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime) {
        super(actionId, CampaignControllerActionType.REDEEM_REWARD, quality, enabled, componentReferences);
        this.rewardId = rewardId;
        this.data = data;
        this.partnerEventId = partnerEventId;
        this.eventTime = eventTime;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(PARTNER_EVENT_ID)
    public Optional<String> getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(EVENT_TIME)
    public RuntimeEvaluatable<RewardActionContext, Optional<Instant>> getEventTime() {
        return eventTime;
    }

}
