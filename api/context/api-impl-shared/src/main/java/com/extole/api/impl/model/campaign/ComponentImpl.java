package com.extole.api.impl.model.campaign;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.Component;
import com.extole.api.model.campaign.ComponentAsset;
import com.extole.api.model.campaign.Variable;

public final class ComponentImpl implements Component {
    private final com.extole.model.entity.campaign.CampaignComponent campaignComponent;

    public ComponentImpl(com.extole.model.entity.campaign.CampaignComponent campaignComponent) {
        this.campaignComponent = campaignComponent;
    }

    @Override
    public String getId() {
        return campaignComponent.getId().getValue();
    }

    @Override
    public String getComponentVersion() {
        return campaignComponent.getComponentVersion();
    }

    @Override
    public String getName() {
        return campaignComponent.getName();
    }

    @Nullable
    @Override
    public String getType() {
        return campaignComponent.getType().orElse(null);
    }

    @Nullable
    @Override
    public String getDescription() {
        return campaignComponent.getDescription().orElse(null);
    }

    @Override
    public String[] getTags() {
        return campaignComponent.getTags().toArray(String[]::new);
    }

    @Override
    public Variable[] getVariables() {
        return campaignComponent.getSettings().stream()
            .filter(value -> value instanceof com.extole.model.entity.campaign.Variable)
            .map(value -> new VariableImpl((com.extole.model.entity.campaign.Variable) value))
            .toArray(Variable[]::new);
    }

    @Override
    public ComponentAsset[] getAssets() {
        return campaignComponent.getAssets().stream()
            .map(value -> new ComponentAssetImpl(value))
            .toArray(ComponentAsset[]::new);
    }

    @Override
    public String getCreatedDate() {
        return campaignComponent.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return campaignComponent.getUpdatedDate().toString();
    }
}
