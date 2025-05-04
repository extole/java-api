package com.extole.api.impl.model.reward.supplier;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.reward.supplier.ManualCouponRewardSupplier;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.event.model.change.reward.supplier.ManualCouponRewardSupplierPojo;

public final class ManualCouponRewardSupplierImpl implements ManualCouponRewardSupplier {

    private final ManualCouponRewardSupplierPojo manualCouponRewardSupplier;

    public ManualCouponRewardSupplierImpl(ManualCouponRewardSupplierPojo manualCouponRewardSupplier) {
        this.manualCouponRewardSupplier = manualCouponRewardSupplier;
    }

    @Override
    public String getId() {
        return manualCouponRewardSupplier.getId().getValue();
    }

    @Override
    public int getCouponCountWarnLimit() {
        return manualCouponRewardSupplier.getCouponCountWarnLimit().intValue();
    }

    @Override
    public Long getMinimumCouponLifetime() {
        return manualCouponRewardSupplier.getMinimumCouponLifetime();
    }

    @Nullable
    @Override
    public String getDefaultCouponExpiryDate() {
        return manualCouponRewardSupplier.getDefaultCouponExpiryDate()
            .map(value -> value.toString())
            .orElse(null);
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return manualCouponRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return manualCouponRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType() {
        return Evaluatables.remapClassToClass(manualCouponRewardSupplier.getFaceValueType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>
        getFaceValueAlgorithmType() {
        return Evaluatables.remapClassToClass(manualCouponRewardSupplier.getFaceValueAlgorithmType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return manualCouponRewardSupplier.getFaceValue();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>
        getCashBackPercentage() {
        return manualCouponRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return manualCouponRewardSupplier.getMinCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return manualCouponRewardSupplier.getMaxCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay() {
        return Evaluatables.remapClassToClass(manualCouponRewardSupplier.getLimitPerDay(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour() {
        return Evaluatables.remapClassToClass(manualCouponRewardSupplier.getLimitPerHour(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return manualCouponRewardSupplier.getName();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(manualCouponRewardSupplier.getDescription(), new TypeReference<>() {});
    }

    @Override
    public String getCreatedDate() {
        return manualCouponRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return manualCouponRewardSupplier.getUpdatedAt().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
