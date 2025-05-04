package com.extole.api.impl.event.reward;

import com.extole.common.lang.ToString;

public final class FailedPublicRewardImpl extends PublicRewardImpl {

    public FailedPublicRewardImpl(com.extole.event.webhook.reward.FailedPublicReward failedRewardEvent) {
        super(failedRewardEvent);
    }

    @Override
    public String getType() {
        return Type.FAILED.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
