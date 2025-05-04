package com.extole.api.impl.event.reward;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.ObjectUtils;

import com.extole.api.event.Sandbox;
import com.extole.api.event.reward.RewardEvent;
import com.extole.api.impl.event.SandboxImpl;
import com.extole.common.lang.ToString;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;

public final class RewardEventImpl implements RewardEvent {

    private final String type;
    private final String id;
    private final String eventTime;
    private final String rewardId;
    private final String rewardName;
    private final String causeEventId;
    private final String rootEventId;
    private final String rewardSupplierName;
    private final String rewardSupplierId;
    private final Optional<String> partnerRewardSupplierId;
    private final String partnerRewardKeyType;
    private final String rewardSupplierType;
    private final String deviceProfileId;
    private final Optional<String> identityProfileId;
    private final Optional<String> partnerUserId;
    private final BigDecimal faceValue;
    private final String faceValueType;
    private final Optional<String> message;
    private final String clientId;
    private final String campaignId;
    private final String programLabel;
    private final String clientDomainId;
    private final Map<String, Object> data;
    private final String[] tags;
    private final Sandbox sandbox;

    private RewardEventImpl(com.extole.event.reward.RewardEvent rewardEvent) {
        this.type = rewardEvent.getType();
        this.id = rewardEvent.getEventId().getValue();
        this.eventTime = ExtoleDateTimeFormatters.ISO_INSTANT.format(rewardEvent.getEventTime());
        this.rewardId = rewardEvent.getRewardId().getValue();
        this.rewardName = rewardEvent.getRewardName();
        this.causeEventId = rewardEvent.getCauseEventId().getValue();
        this.rootEventId = rewardEvent.getRootEventId().getValue();
        this.rewardSupplierName = rewardEvent.getRewardSupplierName();
        this.rewardSupplierId = rewardEvent.getRewardSupplierId().getValue();
        this.partnerRewardSupplierId = rewardEvent.getPartnerRewardSupplierId();
        this.partnerRewardKeyType = rewardEvent.getPartnerRewardKeyType();
        this.rewardSupplierType = rewardEvent.getRewardSupplierType();
        this.deviceProfileId = rewardEvent.getDeviceProfileId().getValue();
        this.identityProfileId = rewardEvent.getIdentityProfileId().map(Id::getValue);
        this.partnerUserId = rewardEvent.getPartnerUserId();
        this.faceValue = rewardEvent.getFaceValue();
        this.faceValueType = rewardEvent.getFaceValueType();
        this.message = rewardEvent.getMessage();
        this.clientId = rewardEvent.getClientId().getValue();
        this.campaignId = rewardEvent.getContext().getCampaignId().getValue();
        this.programLabel = rewardEvent.getContext().getProgramLabel();
        this.clientDomainId = rewardEvent.getContext().getClientDomainId().getValue();
        this.sandbox = new SandboxImpl(rewardEvent.getContext().getSandbox(), rewardEvent.getContext().getContainer());
        this.data = rewardEvent.getData() != null ? ImmutableMap.copyOf(rewardEvent.getData()) : ImmutableMap.of();
        this.tags = rewardEvent.getTags().toArray(new String[] {});
    }

    public static RewardEventImpl newInstance(com.extole.event.reward.RewardEvent rewardEvent) {
        return new RewardEventImpl(rewardEvent);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEventTime() {
        return eventTime;
    }

    @Override
    public String getRewardId() {
        return rewardId;
    }

    @Override
    public String getRewardName() {
        return rewardName;
    }

    @Override
    public String getCauseEventId() {
        return causeEventId;
    }

    @Override
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public String getRewardSupplierName() {
        return rewardSupplierName;
    }

    @Override
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @Nullable
    @Override
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId.orElse(null);
    }

    @Override
    public String getPartnerRewardKeyType() {
        return partnerRewardKeyType;
    }

    @Override
    public String getRewardSupplierType() {
        return rewardSupplierType;
    }

    @Override
    public String getPersonId() {
        return ObjectUtils.firstNonNull(deviceProfileId, identityProfileId.orElse(null));
    }

    @Override
    public String getDeviceProfileId() {
        return deviceProfileId;
    }

    @Nullable
    @Override
    public String getIdentityProfileId() {
        return identityProfileId.orElse(null);
    }

    @Nullable
    @Override
    public String getPartnerUserId() {
        return partnerUserId.orElse(null);
    }

    @Override
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @Override
    public String getFaceValueType() {
        return faceValueType;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message.orElse(null);
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getClientDomainId() {
        return clientDomainId;
    }

    @Override
    public String getContainer() {
        return getSandbox().getContainer();
    }

    @Override
    public Sandbox getSandbox() {
        return sandbox;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
