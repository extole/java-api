package com.extole.api.impl.model.reward.supplier;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.reward.supplier.TangoRewardSupplier;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.event.model.change.reward.supplier.TangoRewardSupplierPojo;

public final class TangoRewardSupplierImpl implements TangoRewardSupplier {

    private final TangoRewardSupplierPojo tangoRewardSupplier;

    public TangoRewardSupplierImpl(TangoRewardSupplierPojo tangoRewardSupplier) {
        this.tangoRewardSupplier = tangoRewardSupplier;
    }

    @Override
    public String getId() {
        return tangoRewardSupplier.getId().getValue();
    }

    @Override
    public String getUtid() {
        return tangoRewardSupplier.getUtid();
    }

    @Override
    public String getAccountId() {
        return tangoRewardSupplier.getAccountId().getValue();
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return tangoRewardSupplier.getPartnerRewardSupplierId().orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return tangoRewardSupplier.getPartnerRewardKeyType().name();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueType() {
        return Evaluatables.remapClassToClass(tangoRewardSupplier.getFaceValueType(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getFaceValueAlgorithmType() {
        return Evaluatables.remapClassToClass(tangoRewardSupplier.getFaceValueAlgorithmType(),
            new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return tangoRewardSupplier.getFaceValue();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage() {
        return tangoRewardSupplier.getCashBackPercentage();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return tangoRewardSupplier.getMinCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return tangoRewardSupplier.getMaxCashBack();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerDay() {
        return Evaluatables.remapClassToClass(tangoRewardSupplier.getLimitPerDay(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Integer> getLimitPerHour() {
        return Evaluatables.remapClassToClass(tangoRewardSupplier.getLimitPerHour(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return tangoRewardSupplier.getName();
    }

    @Override
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(tangoRewardSupplier.getDescription(), new TypeReference<>() {});
    }

    @Override
    public String getCreatedDate() {
        return tangoRewardSupplier.getCreatedAt().toString();
    }

    @Override
    public String getUpdatedDate() {
        return tangoRewardSupplier.getUpdatedAt().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
