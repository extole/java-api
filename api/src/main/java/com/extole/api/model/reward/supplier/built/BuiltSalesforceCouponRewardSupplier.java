package com.extole.api.model.reward.supplier.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.model.EventEntity;

@Schema
public interface BuiltSalesforceCouponRewardSupplier extends EventEntity {
    int getBalanceRefillAmount();

    int getInitialOffset();

    String getCouponPoolId();

    String getSettingsId();

    @Nullable
    String getPartnerRewardSupplierId();

    String getFaceValueType();

    String getFaceValueAlgorithmType();

    BigDecimal getFaceValue();

    BigDecimal getCashBackPercentage();

    BigDecimal getMinCashBack();

    BigDecimal getMaxCashBack();

    @Nullable
    Integer getLimitPerDay();

    @Nullable
    Integer getLimitPerHour();

    String getName();

    @Nullable
    String getDescription();

    String getCreatedDate();

    String getUpdatedDate();
}
