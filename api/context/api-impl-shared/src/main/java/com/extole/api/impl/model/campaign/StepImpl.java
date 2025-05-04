package com.extole.api.impl.model.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.ControllerTrigger;
import com.extole.api.model.campaign.Step;
import com.extole.evaluateable.BuildtimeEvaluatable;

public final class StepImpl implements Step {
    private final com.extole.model.entity.campaign.CampaignStep campaignStep;

    public StepImpl(com.extole.model.entity.campaign.CampaignStep campaignStep) {
        this.campaignStep = campaignStep;
    }

    @Override
    public String getType() {
        return campaignStep.getType().name();
    }

    @Override
    public String getId() {
        return campaignStep.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return campaignStep.getEnabled();
    }

    @Override
    public ControllerTrigger[] getTriggers() {
        return campaignStep.getTriggers().stream()
            .map(value -> new ControllerTriggerImpl(value))
            .toArray(ControllerTrigger[]::new);
    }

    @Override
    public String getCreatedDate() {
        return campaignStep.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return campaignStep.getUpdatedDate().toString();
    }
}
