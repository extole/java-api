package com.extole.api.impl.event.reward;

import com.extole.common.lang.ToString;

public final class CanceledPublicRewardImpl extends PublicRewardImpl {

    public CanceledPublicRewardImpl(com.extole.event.webhook.reward.CanceledPublicReward canceledPublicReward) {
        super(canceledPublicReward);
    }

    @Override
    public String getType() {
        return Type.CANCELED.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
