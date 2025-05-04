package com.extole.api.impl.model.campaign.built;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltCampaignComponent;
import com.extole.api.model.campaign.built.BuiltCampaignComponentAsset;
import com.extole.api.model.campaign.built.BuiltVariable;

public final class BuiltCampaignComponentImpl implements BuiltCampaignComponent {
    private final com.extole.model.entity.campaign.built.BuiltCampaignComponent campaignComponent;

    public BuiltCampaignComponentImpl(com.extole.model.entity.campaign.built.BuiltCampaignComponent campaignComponent) {
        this.campaignComponent = campaignComponent;
    }

    @Override
    public String getId() {
        return campaignComponent.getId().getValue();
    }

    @Nullable
    @Override
    public String getType() {
        return campaignComponent.getType().orElse(null);
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
    public String getDescription() {
        return campaignComponent.getDescription().orElse(null);
    }

    @Override
    public String[] getTags() {
        return campaignComponent.getTags().toArray(String[]::new);
    }

    @Override
    public BuiltVariable[] getVariables() {
        return campaignComponent.getSettings().stream()
            .filter(value -> value instanceof BuiltVariable)
            .map(value -> new BuiltVariableImpl((com.extole.model.entity.campaign.built.BuiltVariable) value))
            .toArray(BuiltVariable[]::new);
    }

    @Override
    public BuiltCampaignComponentAsset[] getAssets() {
        return campaignComponent.getAssets().stream().map(value -> new BuiltCampaignComponentAssetImpl(value))
            .toArray(BuiltCampaignComponentAsset[]::new);
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
