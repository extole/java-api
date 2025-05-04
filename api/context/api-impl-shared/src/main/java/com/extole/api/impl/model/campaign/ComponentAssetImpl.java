package com.extole.api.impl.model.campaign;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.ComponentAsset;

final class ComponentAssetImpl implements ComponentAsset {
    private final com.extole.model.entity.campaign.CampaignComponentAsset campaignComponentAsset;

    ComponentAssetImpl(com.extole.model.entity.campaign.CampaignComponentAsset campaignComponentAsset) {
        this.campaignComponentAsset = campaignComponentAsset;
    }

    @Override
    public String getId() {
        return campaignComponentAsset.getId().getValue();
    }

    @Override
    public String getName() {
        return campaignComponentAsset.getName();
    }

    @Override
    public String getFilename() {
        return campaignComponentAsset.getFilename();
    }

    @Override
    public String[] getTags() {
        return campaignComponentAsset.getTags().toArray(String[]::new);
    }

    @Nullable
    @Override
    public String getDescription() {
        return campaignComponentAsset.getDescription().orElse(null);
    }
}
