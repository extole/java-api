package com.extole.api.impl.event.reward;

import javax.annotation.Nullable;

import com.extole.api.webhook.reward.event.RedeemedPublicReward;
import com.extole.common.lang.ToString;

public final class RedeemedPublicRewardImpl extends PublicRewardImpl implements RedeemedPublicReward {
    private final String partnerEventId;

    public RedeemedPublicRewardImpl(com.extole.event.webhook.reward.RedeemedPublicReward redeemedRewardEvent) {
        super(redeemedRewardEvent);
        this.partnerEventId = redeemedRewardEvent.getPartnerEventId().orElse(null);
    }

    @Override
    @Nullable
    public String getPartnerEventId() {
        return partnerEventId;
    }

    @Override
    public String getType() {
        return Type.REDEEMED.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
