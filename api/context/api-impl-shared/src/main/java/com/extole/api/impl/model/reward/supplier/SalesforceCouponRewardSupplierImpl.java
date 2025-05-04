package com.extole.api.impl.model.reward.supplier;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.reward.supplier.SalesforceCouponRewardSupplier;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.event.model.change.reward.supplier.SalesforceCouponRewardSupplierPojo;

public final class SalesforceCouponRewardSupplierImpl implements SalesforceCouponRewardSupplier {

    private final SalesforceCouponRewardSupplierPojo salesforceCouponRewardSupplier;

    public SalesforceCouponRewardSupplierImpl(SalesforceCouponRewardSupplierPojo salesforceCouponRewardSupplier) {
        this.salesforceCouponRewardSupplier = salesforceCouponRewardSupplier;
    }

    @Override
    public String getId() {
        return salesforceCouponRewardSupplier.getId().getValue();
    }

    @Override
    public int getBalanceRefillAmount() {
        return salesforceCouponRewardSupplier.getBalanceRefillAmount().intValue();
    }

    @Override
    public int getInitialOffset() {
        return salesforceCouponRewardSupplier.getInitialOffset().intValue();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getCouponPoolId() {
        return salesforceCouponRewardSupplier.getCouponPoolId();
    }

    @Override
    public String getSettingsId() {
        return salesforceCouponRewardSupplier.getSettingsId().getValue();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return salesforceCouponRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return salesforceCouponRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType() {
        return Evaluatables.remapClassToClass(salesforceCouponRewardSupplier.getFaceValueType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueAlgorithmType() {
        return Evaluatables.remapClassToClass(salesforceCouponRewardSupplier.getFaceValueAlgorithmType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return salesforceCouponRewardSupplier.getFaceValue();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage() {
        return salesforceCouponRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return salesforceCouponRewardSupplier.getMinCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return salesforceCouponRewardSupplier.getMaxCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay() {
        return Evaluatables.remapClassToClass(salesforceCouponRewardSupplier.getLimitPerDay(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour() {
        return Evaluatables.remapClassToClass(salesforceCouponRewardSupplier.getLimitPerHour(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return salesforceCouponRewardSupplier.getName();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(salesforceCouponRewardSupplier.getDescription(),
            new TypeReference<>() {});
    }

    @Override
    public String getCreatedDate() {
        return salesforceCouponRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return salesforceCouponRewardSupplier.getUpdatedAt().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
