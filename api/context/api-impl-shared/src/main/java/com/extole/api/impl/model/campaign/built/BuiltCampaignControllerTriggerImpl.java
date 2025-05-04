package com.extole.api.impl.model.campaign.built;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltCampaignControllerTrigger;

final class BuiltCampaignControllerTriggerImpl implements BuiltCampaignControllerTrigger {
    private final com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger campaignControllerTrigger;

    BuiltCampaignControllerTriggerImpl(
        com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger campaignControllerTrigger) {
        this.campaignControllerTrigger = campaignControllerTrigger;
    }

    @Override
    public String getId() {
        return campaignControllerTrigger.getId().getValue();
    }

    @Override
    public String getType() {
        return campaignControllerTrigger.getType().name();
    }

    @Override
    public String getPhase() {
        return campaignControllerTrigger.getPhase().name();
    }

    @Override
    public String getName() {
        return campaignControllerTrigger.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return campaignControllerTrigger.getDescription().orElse(null);
    }

    @Override
    public boolean getEnabled() {
        return campaignControllerTrigger.getEnabled().booleanValue();
    }

    @Override
    public boolean getNegated() {
        return campaignControllerTrigger.getNegated().booleanValue();
    }

    @Override
    public String getCreatedDate() {
        return campaignControllerTrigger.getCreatedDate().toString();
    }
}
