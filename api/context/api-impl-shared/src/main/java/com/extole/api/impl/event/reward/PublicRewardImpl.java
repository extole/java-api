package com.extole.api.impl.event.reward;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.webhook.reward.event.PublicReward;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public abstract class PublicRewardImpl implements PublicReward {

    private final Id<?> rewardId;
    private final String rewardName;
    private final String rewardSupplierName;
    private final Id<?> rewardSupplierId;
    private final String partnerRewardSupplierId;
    private final String partnerRewardKeyType;
    private final String rewardSupplierType;
    private final Id<?> personId;
    private final String partnerUserId;
    private final BigDecimal faceValue;
    private final FaceValueType faceValueType;
    private final String partnerRewardId;
    private final Map<String, Object> data;

    protected PublicRewardImpl(com.extole.event.webhook.reward.PublicReward publicReward) {
        this.rewardId = publicReward.getRewardId();
        this.rewardName = publicReward.getRewardName();
        this.rewardSupplierName = publicReward.getRewardSupplierName();
        this.rewardSupplierId = publicReward.getRewardSupplierId();
        this.partnerRewardSupplierId = publicReward.getPartnerRewardSupplierId().orElse(null);
        this.partnerRewardKeyType = publicReward.getPartnerRewardKeyType();
        this.rewardSupplierType = publicReward.getRewardSupplierType();
        this.personId = publicReward.getDeviceProfileId();
        this.partnerUserId = publicReward.getPartnerUserId().orElse(null);
        this.faceValue = publicReward.getFaceValue();
        this.faceValueType = FaceValueType.valueOf(publicReward.getFaceValueType());
        this.partnerRewardId = publicReward.getPartnerRewardId().orElse(null);
        this.data = ImmutableMap.copyOf(publicReward.getData());
    }

    @Override
    public String getRewardId() {
        return rewardId.getValue();
    }

    @Override
    public String getRewardName() {
        return rewardName;
    }

    @Override
    public String getRewardSupplierName() {
        return rewardSupplierName;
    }

    @Override
    public String getRewardSupplierId() {
        return rewardSupplierId.getValue();
    }

    @Override
    @Nullable
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    public String getPartnerRewardKeyType() {
        return partnerRewardKeyType;
    }

    @Override
    public String getRewardSupplierType() {
        return rewardSupplierType;
    }

    @Override
    public String getPersonId() {
        return personId.getValue();
    }

    @Override
    @Nullable
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @Override
    public String getFaceValue() {
        return faceValue.toString();
    }

    @Override
    public String getFaceValueType() {
        return faceValueType.name();
    }

    @Override
    @Nullable
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
