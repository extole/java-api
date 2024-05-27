package com.extole.api.webhook.reward.event;

import java.util.Map;

import javax.annotation.Nullable;

public interface PublicReward {

    enum Type {
        EARNED, FULFILLED, SENT, REDEEMED, FAILED, FAILED_FULFILLMENT, CANCELED, REVOKED
    }

    enum FaceValueType {
        PERCENT_OFF, POINTS, MONTH, USD, GBP, EUR, JPY, CNY, CAD, AUD, BRL, INR, NZD, MXN, KRW, TWD, TRY, HKD
    }

    String getType();

    String getRewardId();

    String getRewardName();

    String getRewardSupplierName();

    String getRewardSupplierId();

    @Nullable
    String getPartnerRewardSupplierId();

    String getRewardSupplierType();

    String getPersonId();

    @Nullable
    String getPartnerUserId();

    String getFaceValue();

    String getFaceValueType();

    @Nullable
    String getPartnerRewardId();

    Map<String, Object> getData();

}
