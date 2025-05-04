package com.extole.api.impl.model.reward.supplier.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.reward.supplier.built.BuiltPayPalPayoutsRewardSupplier;
import com.extole.event.model.change.reward.supplier.built.BuiltPayPalPayoutsRewardSupplierPojo;

public final class BuiltPayPalPayoutsRewardSupplierImpl implements BuiltPayPalPayoutsRewardSupplier {
    private final BuiltPayPalPayoutsRewardSupplierPojo builtPayPalPayoutsRewardSupplier;

    public BuiltPayPalPayoutsRewardSupplierImpl(BuiltPayPalPayoutsRewardSupplierPojo builtPayPalPayoutsRewardSupplier) {
        this.builtPayPalPayoutsRewardSupplier = builtPayPalPayoutsRewardSupplier;
    }

    @Override
    public String getId() {
        return builtPayPalPayoutsRewardSupplier.getId().getValue();
    }

    @Override
    public String getMerchantToken() {
        return builtPayPalPayoutsRewardSupplier.getMerchantToken();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return builtPayPalPayoutsRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return builtPayPalPayoutsRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public String getFaceValueType() {
        return builtPayPalPayoutsRewardSupplier.getFaceValueType().name();
    }

    @Override
    public String getFaceValueAlgorithmType() {
        return builtPayPalPayoutsRewardSupplier.getFaceValueType().name();
    }

    @Override
    public BigDecimal getFaceValue() {
        return builtPayPalPayoutsRewardSupplier.getFaceValue();
    }

    @Override
    public BigDecimal getCashBackPercentage() {
        return builtPayPalPayoutsRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BigDecimal getMinCashBack() {
        return builtPayPalPayoutsRewardSupplier.getMinCashBack();
    }

    @Override
    public BigDecimal getMaxCashBack() {
        return builtPayPalPayoutsRewardSupplier.getMaxCashBack();
    }

    @Nullable
    @Override
    public Integer getLimitPerDay() {
        return builtPayPalPayoutsRewardSupplier.getLimitPerDay().orElse(null);
    }

    @Nullable
    @Override
    public Integer getLimitPerHour() {
        return builtPayPalPayoutsRewardSupplier.getLimitPerHour().orElse(null);
    }

    @Override
    public String getName() {
        return builtPayPalPayoutsRewardSupplier.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return builtPayPalPayoutsRewardSupplier.getDescription().orElse(null);
    }

    @Override
    public String getCreatedDate() {
        return builtPayPalPayoutsRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtPayPalPayoutsRewardSupplier.getUpdatedAt().toString();
    }
}
