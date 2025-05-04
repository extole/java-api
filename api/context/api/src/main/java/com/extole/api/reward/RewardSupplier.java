package com.extole.api.reward;

import javax.annotation.Nullable;

import com.extole.api.component.ComponentReference;

public interface RewardSupplier {

    String getRewardSupplierId();

    @Nullable
    String getPartnerRewardSupplierId();

    String getPartnerRewardKeyType();

    String getFaceValueType();

    String getFaceValue();

    String getName();

    String getType();

    String getDisplayType();

    ComponentReference[] getComponentReferences();
}
