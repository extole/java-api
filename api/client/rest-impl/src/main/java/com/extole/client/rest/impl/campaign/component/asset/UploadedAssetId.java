package com.extole.client.rest.impl.campaign.component.asset;

import java.util.Objects;

public class UploadedAssetId {
    private final String componentAbsoluteName;
    private final String assetName;

    public UploadedAssetId(String componentAbsoluteName, String assetName) {
        this.componentAbsoluteName = componentAbsoluteName;
        this.assetName = assetName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        UploadedAssetId that = (UploadedAssetId) other;
        return Objects.equals(componentAbsoluteName, that.componentAbsoluteName)
            && Objects.equals(assetName.toLowerCase(), that.assetName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentAbsoluteName, assetName.toLowerCase());
    }
}
