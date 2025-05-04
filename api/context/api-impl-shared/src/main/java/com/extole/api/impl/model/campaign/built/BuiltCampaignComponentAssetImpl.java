package com.extole.api.impl.model.campaign.built;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltCampaignComponentAsset;

public final class BuiltCampaignComponentAssetImpl implements BuiltCampaignComponentAsset {
    private final com.extole.model.entity.campaign.built.BuiltCampaignComponentAsset campaignComponentAsset;

    public BuiltCampaignComponentAssetImpl(
        com.extole.model.entity.campaign.built.BuiltCampaignComponentAsset campaignComponentAsset) {
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
