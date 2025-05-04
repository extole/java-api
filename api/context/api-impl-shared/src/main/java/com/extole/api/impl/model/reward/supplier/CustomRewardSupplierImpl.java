package com.extole.api.impl.model.reward.supplier;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.reward.supplier.CustomRewardSupplier;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.event.model.change.reward.supplier.CustomRewardSupplierPojo;

public final class CustomRewardSupplierImpl implements CustomRewardSupplier {

    private final CustomRewardSupplierPojo customRewardSupplier;

    public CustomRewardSupplierImpl(CustomRewardSupplierPojo customRewardSupplier) {
        this.customRewardSupplier = customRewardSupplier;
    }

    @Override
    public String getId() {
        return customRewardSupplier.getId().getValue();
    }

    @Override
    public String getType() {
        return customRewardSupplier.getType().name();
    }

    @Override
    public boolean isRewardEmailAutoSendEnabled() {
        return customRewardSupplier.isRewardEmailAutoSendEnabled();
    }

    @Override
    public boolean isAutoFulfillmentEnabled() {
        return customRewardSupplier.isAutoFulfillmentEnabled();
    }

    @Override
    public boolean isMissingFulfillmentAlertEnabled() {
        return customRewardSupplier.isMissingFulfillmentAlertEnabled();
    }

    @Override
    public long getMissingFulfillmentAlertDelay() {
        return customRewardSupplier.getMissingFulfillmentAlertDelayMs().longValue();
    }

    @Override
    public boolean isMissingFulfillmentAutoFailEnabled() {
        return customRewardSupplier.isMissingFulfillmentAutoFailEnabled();
    }

    @Override
    public long getMissingFulfillmentAutoFailDelay() {
        return customRewardSupplier.getMissingFulfillmentAutoFailDelayMs().longValue();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return customRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return customRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType() {
        return Evaluatables.remapClassToClass(customRewardSupplier.getFaceValueType(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueAlgorithmType() {
        return Evaluatables.remapClassToClass(customRewardSupplier.getFaceValueAlgorithmType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return customRewardSupplier.getFaceValue();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage() {
        return customRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return customRewardSupplier.getMinCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return customRewardSupplier.getMaxCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay() {
        return Evaluatables.remapClassToClass(customRewardSupplier.getLimitPerDay(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour() {
        return Evaluatables.remapClassToClass(customRewardSupplier.getLimitPerHour(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return customRewardSupplier.getName();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(customRewardSupplier.getDescription(), new TypeReference<>() {});
    }

    @Override
    public String getCreatedDate() {
        return customRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return customRewardSupplier.getUpdatedAt().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
