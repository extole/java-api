package com.extole.api.impl.model.reward.supplier.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.reward.supplier.built.BuiltManualCouponRewardSupplier;
import com.extole.event.model.change.reward.supplier.built.BuiltManualCouponRewardSupplierPojo;

public final class BuiltManualCouponRewardSupplierImpl implements BuiltManualCouponRewardSupplier {
    private final BuiltManualCouponRewardSupplierPojo builtManualCouponRewardSupplier;

    public BuiltManualCouponRewardSupplierImpl(BuiltManualCouponRewardSupplierPojo builtManualCouponRewardSupplier) {
        this.builtManualCouponRewardSupplier = builtManualCouponRewardSupplier;
    }

    @Override
    public String getId() {
        return builtManualCouponRewardSupplier.getId().getValue();
    }

    @Override
    public int getCouponCountWarnLimit() {
        return builtManualCouponRewardSupplier.getCouponCountWarnLimit().intValue();
    }

    @Override
    public Long getMinimumCouponLifetime() {
        return builtManualCouponRewardSupplier.getMinimumCouponLifetime();
    }

    @Nullable
    @Override
    public String getDefaultCouponExpiryDate() {
        return builtManualCouponRewardSupplier.getDefaultCouponExpiryDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return builtManualCouponRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return builtManualCouponRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public String getFaceValueType() {
        return builtManualCouponRewardSupplier.getFaceValueType().name();
    }

    @Override
    public String getFaceValueAlgorithmType() {
        return builtManualCouponRewardSupplier.getFaceValueAlgorithmType().name();
    }

    @Override
    public BigDecimal getFaceValue() {
        return builtManualCouponRewardSupplier.getFaceValue();
    }

    @Override
    public BigDecimal getCashBackPercentage() {
        return builtManualCouponRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BigDecimal getMinCashBack() {
        return builtManualCouponRewardSupplier.getMinCashBack();
    }

    @Override
    public BigDecimal getMaxCashBack() {
        return builtManualCouponRewardSupplier.getMaxCashBack();
    }

    @Nullable
    @Override
    public Integer getLimitPerDay() {
        return builtManualCouponRewardSupplier.getLimitPerDay().orElse(null);
    }

    @Nullable
    @Override
    public Integer getLimitPerHour() {
        return builtManualCouponRewardSupplier.getLimitPerHour().orElse(null);
    }

    @Override
    public String getName() {
        return builtManualCouponRewardSupplier.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return builtManualCouponRewardSupplier.getDescription().orElse(null);
    }

    @Override
    public String getCreatedDate() {
        return builtManualCouponRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtManualCouponRewardSupplier.getUpdatedAt().toString();
    }
}
