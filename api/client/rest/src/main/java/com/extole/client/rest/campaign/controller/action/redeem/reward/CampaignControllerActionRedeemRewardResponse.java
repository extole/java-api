package com.extole.client.rest.campaign.controller.action.redeem.reward;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.REDEEM_REWARD;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionRedeemRewardResponse extends CampaignControllerActionResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String DATA = "data";
    private static final String PARTNER_EVENT_ID = "partner_event_id";
    private static final String EVENT_TIME = "event_time";

    private final String rewardId;
    private final Map<String, String> data;
    private final Optional<String> partnerEventId;
    private final RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime;

    public CampaignControllerActionRedeemRewardResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(PARTNER_EVENT_ID) Optional<String> partnerEventId,
        @JsonProperty(EVENT_TIME) RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime) {
        super(actionId, REDEEM_REWARD, quality, enabled, componentIds, componentReferences);
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
