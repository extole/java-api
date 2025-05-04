package com.extole.api.impl.model.reward.supplier;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.reward.supplier.PayPalPayoutsRewardSupplier;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.event.model.change.reward.supplier.PayPalPayoutsRewardSupplierPojo;

public final class PayPalPayoutsRewardSupplierImpl implements PayPalPayoutsRewardSupplier {

    private final PayPalPayoutsRewardSupplierPojo payPalPayoutsRewardSupplier;

    public PayPalPayoutsRewardSupplierImpl(PayPalPayoutsRewardSupplierPojo payPalPayoutsRewardSupplier) {
        this.payPalPayoutsRewardSupplier = payPalPayoutsRewardSupplier;
    }

    @Override
    public String getId() {
        return payPalPayoutsRewardSupplier.getId().getValue();
    }

    @Override
    public String getMerchantToken() {
        return payPalPayoutsRewardSupplier.getMerchantToken();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return payPalPayoutsRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return payPalPayoutsRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType() {
        return Evaluatables.remapClassToClass(payPalPayoutsRewardSupplier.getFaceValueType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueAlgorithmType() {
        return Evaluatables.remapClassToClass(payPalPayoutsRewardSupplier.getFaceValueAlgorithmType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return payPalPayoutsRewardSupplier.getFaceValue();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage() {
        return payPalPayoutsRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return payPalPayoutsRewardSupplier.getMinCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return payPalPayoutsRewardSupplier.getMaxCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay() {
        return Evaluatables.remapClassToClass(payPalPayoutsRewardSupplier.getLimitPerDay(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour() {
        return Evaluatables.remapClassToClass(payPalPayoutsRewardSupplier.getLimitPerHour(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return payPalPayoutsRewardSupplier.getName();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(payPalPayoutsRewardSupplier.getDescription(), new TypeReference<>() {});
    }

    @Override
    public String getCreatedDate() {
        return payPalPayoutsRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return payPalPayoutsRewardSupplier.getUpdatedAt().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
