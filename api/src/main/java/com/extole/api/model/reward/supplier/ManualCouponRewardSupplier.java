package com.extole.api.model.reward.supplier;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.model.EventEntity;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

@Schema
public interface ManualCouponRewardSupplier extends EventEntity {
    int getCouponCountWarnLimit();

    Long getMinimumCouponLifetime();

    @Nullable
    String getDefaultCouponExpiryDate();

    @Nullable
    String getPartnerRewardSupplierId();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueAlgorithmType();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription();

    String getCreatedDate();

    String getUpdatedDate();
}
