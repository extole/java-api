package com.extole.api.impl.model.reward.supplier.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.reward.supplier.built.BuiltTangoRewardSupplier;
import com.extole.event.model.change.reward.supplier.built.BuiltTangoRewardSupplierPojo;

public final class BuiltTangoRewardSupplierImpl implements BuiltTangoRewardSupplier {
    private final BuiltTangoRewardSupplierPojo builtTangoRewardSupplier;

    public BuiltTangoRewardSupplierImpl(BuiltTangoRewardSupplierPojo builtTangoRewardSupplier) {
        this.builtTangoRewardSupplier = builtTangoRewardSupplier;
    }

    @Override
    public String getId() {
        return builtTangoRewardSupplier.getId().getValue();
    }

    @Override
    public String getUtid() {
        return builtTangoRewardSupplier.getUtid();
    }

    @Override
    public String getAccountId() {
        return builtTangoRewardSupplier.getAccountId().getValue();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return builtTangoRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return builtTangoRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public String getFaceValueType() {
        return builtTangoRewardSupplier.getFaceValueType().name();
    }

    @Override
    public String getFaceValueAlgorithmType() {
        return builtTangoRewardSupplier.getFaceValueAlgorithmType().name();
    }

    @Override
    public BigDecimal getFaceValue() {
        return builtTangoRewardSupplier.getFaceValue();
    }

    @Override
    public BigDecimal getCashBackPercentage() {
        return builtTangoRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BigDecimal getMinCashBack() {
        return builtTangoRewardSupplier.getMinCashBack();
    }

    @Override
    public BigDecimal getMaxCashBack() {
        return builtTangoRewardSupplier.getMaxCashBack();
    }

    @Nullable
    @Override
    public Integer getLimitPerDay() {
        return builtTangoRewardSupplier.getLimitPerDay().orElse(null);
    }

    @Nullable
    @Override
    public Integer getLimitPerHour() {
        return builtTangoRewardSupplier.getLimitPerHour().orElse(null);
    }

    @Override
    public String getName() {
        return builtTangoRewardSupplier.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return builtTangoRewardSupplier.getDescription().orElse(null);
    }

    @Override
    public String getCreatedDate() {
        return builtTangoRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtTangoRewardSupplier.getUpdatedAt().toString();
    }
}
