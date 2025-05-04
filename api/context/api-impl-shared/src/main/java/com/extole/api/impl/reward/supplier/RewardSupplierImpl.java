package com.extole.api.impl.reward.supplier;

import java.util.List;

import com.extole.api.component.ComponentReference;
import com.extole.api.impl.component.ComponentReferenceImpl;
import com.extole.api.reward.RewardSupplier;
import com.extole.model.entity.campaign.built.BuiltCampaignComponentReference;

public class RewardSupplierImpl implements RewardSupplier {
    private final String clientId;
    private final String rewardSupplierId;
    private final String partnerRewardSupplierId;
    private final String partnerRewardKeyType;
    private final String faceValueType;
    private final String faceValue;
    private final String name;
    private final String type;
    private final String displayType;
    private final ComponentReference[] componentReferences;

    public RewardSupplierImpl(String clientId,
        String rewardSupplierId,
        String partnerRewardSupplierId,
        String partnerRewardKeyType,
        String faceValueType,
        String faceValue,
        String name,
        String type,
        String displayType,
        List<BuiltCampaignComponentReference> builtComponentReferences) {
        this.clientId = clientId;
        this.rewardSupplierId = rewardSupplierId;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.partnerRewardKeyType = partnerRewardKeyType;
        this.faceValueType = faceValueType;
        this.faceValue = faceValue;
        this.name = name;
        this.type = type;
        this.displayType = displayType;
        this.componentReferences = builtComponentReferences.stream()
            .map(builtRef -> new ComponentReferenceImpl(builtRef.getComponentId().getValue()))
            .toArray(ComponentReference[]::new);
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @Override
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @Override
    public String getPartnerRewardKeyType() {
        return partnerRewardKeyType;
    }

    @Override
    public String getFaceValueType() {
        return faceValueType;
    }

    @Override
    public String getFaceValue() {
        return faceValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDisplayType() {
        return displayType;
    }

    @Override
    public ComponentReference[] getComponentReferences() {
        return componentReferences;
    }
}
