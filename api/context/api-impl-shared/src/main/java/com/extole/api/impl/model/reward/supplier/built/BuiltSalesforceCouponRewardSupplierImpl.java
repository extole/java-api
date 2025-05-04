package com.extole.api.impl.model.reward.supplier.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.reward.supplier.built.BuiltSalesforceCouponRewardSupplier;
import com.extole.event.model.change.reward.supplier.built.BuiltSalesforceCouponRewardSupplierPojo;

public final class BuiltSalesforceCouponRewardSupplierImpl implements BuiltSalesforceCouponRewardSupplier {
    private final BuiltSalesforceCouponRewardSupplierPojo builtSalesforceCouponRewardSupplier;

    public BuiltSalesforceCouponRewardSupplierImpl(
        BuiltSalesforceCouponRewardSupplierPojo builtSalesforceCouponRewardSupplier) {
        this.builtSalesforceCouponRewardSupplier = builtSalesforceCouponRewardSupplier;
    }

    @Override
    public String getId() {
        return builtSalesforceCouponRewardSupplier.getId().getValue();
    }

    @Override
    public int getBalanceRefillAmount() {
        return builtSalesforceCouponRewardSupplier.getBalanceRefillAmount().intValue();
    }

    @Override
    public int getInitialOffset() {
        return builtSalesforceCouponRewardSupplier.getInitialOffset().intValue();
    }

    @Override
    public String getCouponPoolId() {
        return builtSalesforceCouponRewardSupplier.getCouponPoolId();
    }

    @Override
    public String getSettingsId() {
        return builtSalesforceCouponRewardSupplier.getSettingsId().getValue();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return builtSalesforceCouponRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return builtSalesforceCouponRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public String getFaceValueType() {
        return builtSalesforceCouponRewardSupplier.getFaceValueType().name();
    }

    @Override
    public String getFaceValueAlgorithmType() {
        return builtSalesforceCouponRewardSupplier.getFaceValueAlgorithmType().name();
    }

    @Override
    public BigDecimal getFaceValue() {
        return builtSalesforceCouponRewardSupplier.getFaceValue();
    }

    @Override
    public BigDecimal getCashBackPercentage() {
        return builtSalesforceCouponRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BigDecimal getMinCashBack() {
        return builtSalesforceCouponRewardSupplier.getMinCashBack();
    }

    @Override
    public BigDecimal getMaxCashBack() {
        return builtSalesforceCouponRewardSupplier.getMaxCashBack();
    }

    @Nullable
    @Override
    public Integer getLimitPerDay() {
        return builtSalesforceCouponRewardSupplier.getLimitPerDay().orElse(null);
    }

    @Nullable
    @Override
    public Integer getLimitPerHour() {
        return builtSalesforceCouponRewardSupplier.getLimitPerHour().orElse(null);
    }

    @Override
    public String getName() {
        return builtSalesforceCouponRewardSupplier.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return builtSalesforceCouponRewardSupplier.getDescription().orElse(null);
    }

    @Override
    public String getCreatedDate() {
        return builtSalesforceCouponRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtSalesforceCouponRewardSupplier.getUpdatedAt().toString();
    }
}
