package com.extole.api.impl.event.reward;

import com.extole.common.lang.ToString;

public final class FulfilledPublicRewardImpl extends PublicRewardImpl {

    public FulfilledPublicRewardImpl(com.extole.event.webhook.reward.FulfilledPublicReward fulfilledRewardEvent) {
        super(fulfilledRewardEvent);
    }

    @Override
    public String getType() {
        return Type.FULFILLED.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
