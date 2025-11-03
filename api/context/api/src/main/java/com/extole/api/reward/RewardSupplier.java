package com.extole.api.reward;

import javax.annotation.Nullable;

import com.extole.api.component.ComponentReference;

public interface RewardSupplier {

    String getRewardSupplierId();

    @Nullable
    String getPartnerRewardSupplierId();

    String getPartnerRewardKeyType();

    String getFaceValueType();

    String getFaceValueAlgorithmType();

    String getFaceValue();

    String getCashBackPercentage();

    String getMaxCashBack();

    String getMinCashBack();

    String getName();

    String getType();

    String getDisplayType();

    ComponentReference[] getComponentReferences();
}
