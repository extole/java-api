package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.api.model.campaign.built.BuiltCampaignStep;

final class BuiltCampaignStepImpl implements BuiltCampaignStep {
    private final com.extole.model.entity.campaign.built.BuiltCampaignStep builtCampaignStep;

    BuiltCampaignStepImpl(com.extole.model.entity.campaign.built.BuiltCampaignStep builtCampaignStep) {
        this.builtCampaignStep = builtCampaignStep;
    }

    @Override
    public String getType() {
        return builtCampaignStep.getType().name();
    }

    @Override
    public String getId() {
        return builtCampaignStep.getId().getValue();
    }

    @Override
    public boolean isEnabled() {
        return builtCampaignStep.isEnabled();
    }

    @Override
    public BuiltCampaignControllerTrigger[] getTriggers() {
        return builtCampaignStep.getTriggers().stream()
            .map(value -> new BuiltCampaignControllerTriggerImpl(value))
            .toArray(BuiltCampaignControllerTrigger[]::new);
    }

    @Override
    public String getCreatedDate() {
        return builtCampaignStep.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtCampaignStep.getUpdatedDate().toString();
    }
}
