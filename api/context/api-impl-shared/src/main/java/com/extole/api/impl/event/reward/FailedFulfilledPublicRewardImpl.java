package com.extole.api.impl.event.reward;

import com.extole.common.lang.ToString;

public final class FailedFulfilledPublicRewardImpl extends PublicRewardImpl {

    public FailedFulfilledPublicRewardImpl(
        com.extole.event.webhook.reward.FailedFulfilledPublicReward failedFulfillmentRewardEvent) {
        super(failedFulfillmentRewardEvent);
    }

    @Override
    public String getType() {
        return Type.FAILED_FULFILLMENT.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
