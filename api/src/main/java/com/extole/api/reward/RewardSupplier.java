package com.extole.api.reward;

import javax.annotation.Nullable;

public interface RewardSupplier {

    String getRewardSupplierId();

    @Nullable
    String getPartnerRewardSupplierId();

    String getFaceValueType();

    String getFaceValue();

    String getName();

    String getType();

    String getDisplayType();
}
