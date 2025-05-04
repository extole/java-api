package com.extole.api.impl.event.reward;

import com.extole.common.lang.ToString;

public final class EarnedPublicRewardImpl extends PublicRewardImpl {

    public EarnedPublicRewardImpl(com.extole.event.webhook.reward.EarnedPublicReward earnedRewardEvent) {
        super(earnedRewardEvent);
    }

    @Override
    public String getType() {
        return Type.EARNED.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
