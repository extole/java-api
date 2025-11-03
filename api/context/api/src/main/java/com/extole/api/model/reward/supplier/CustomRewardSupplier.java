package com.extole.api.model.reward.supplier;

import java.math.BigDecimal;
import java.util.Optional;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.model.EventEntity;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

@Schema
public interface CustomRewardSupplier extends EventEntity {
    String getType();

    boolean isRewardEmailAutoSendEnabled();

    boolean isAutoFulfillmentEnabled();

    boolean isMissingFulfillmentAlertEnabled();

    long getMissingFulfillmentAlertDelay();

    boolean isMissingFulfillmentAutoFailEnabled();

    long getMissingFulfillmentAutoFailDelay();

    @Nullable
    String getPartnerRewardSupplierId();

    String getPartnerRewardKeyType();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueAlgorithmType();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> getDisplayName();

    BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription();

    String getCreatedDate();

    String getUpdatedDate();
}
