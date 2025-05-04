package com.extole.api.impl.model.reward.supplier.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.reward.supplier.built.BuiltCustomRewardSupplier;
import com.extole.event.model.change.reward.supplier.built.BuiltCustomRewardSupplierPojo;

public final class BuiltCustomRewardSupplierImpl implements BuiltCustomRewardSupplier {
    private final BuiltCustomRewardSupplierPojo builtCustomRewardSupplier;

    public BuiltCustomRewardSupplierImpl(BuiltCustomRewardSupplierPojo builtCustomRewardSupplier) {
        this.builtCustomRewardSupplier = builtCustomRewardSupplier;
    }

    @Override
    public String getId() {
        return builtCustomRewardSupplier.getId().getValue();
    }

    @Override
    public String getType() {
        return builtCustomRewardSupplier.getType().name();
    }

    @Override
    public boolean isRewardEmailAutoSendEnabled() {
        return builtCustomRewardSupplier.isRewardEmailAutoSendEnabled();
    }

    @Override
    public boolean isAutoFulfillmentEnabled() {
        return builtCustomRewardSupplier.isAutoFulfillmentEnabled();
    }

    @Override
    public boolean isMissingFulfillmentAlertEnabled() {
        return builtCustomRewardSupplier.isMissingFulfillmentAlertEnabled();
    }

    @Override
    public long getMissingFulfillmentAlertDelay() {
        return builtCustomRewardSupplier.getMissingFulfillmentAlertDelayMs().longValue();
    }

    @Override
    public boolean isMissingFulfillmentAutoFailEnabled() {
        return builtCustomRewardSupplier.isMissingFulfillmentAutoFailEnabled();
    }

    @Override
    public long getMissingFulfillmentAutoFailDelay() {
        return builtCustomRewardSupplier.getMissingFulfillmentAutoFailDelayMs().longValue();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return builtCustomRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return builtCustomRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public String getFaceValueType() {
        return builtCustomRewardSupplier.getFaceValueType().name();
    }

    @Override
    public String getFaceValueAlgorithmType() {
        return builtCustomRewardSupplier.getFaceValueAlgorithmType().name();
    }

    @Override
    public BigDecimal getFaceValue() {
        return builtCustomRewardSupplier.getFaceValue();
    }

    @Override
    public BigDecimal getCashBackPercentage() {
        return builtCustomRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BigDecimal getMinCashBack() {
        return builtCustomRewardSupplier.getMinCashBack();
    }

    @Override
    public BigDecimal getMaxCashBack() {
        return builtCustomRewardSupplier.getMaxCashBack();
    }

    @Nullable
    @Override
    public Integer getLimitPerDay() {
        return builtCustomRewardSupplier.getLimitPerDay().orElse(null);
    }

    @Nullable
    @Override
    public Integer getLimitPerHour() {
        return builtCustomRewardSupplier.getLimitPerHour().orElse(null);
    }

    @Override
    public String getName() {
        return builtCustomRewardSupplier.getName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return builtCustomRewardSupplier.getDescription().orElse(null);
    }

    @Override
    public String getCreatedDate() {
        return builtCustomRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtCustomRewardSupplier.getUpdatedAt().toString();
    }
}
