package com.extole.api.impl.event.reward;

import com.extole.common.lang.ToString;

public final class RevokedPublicRewardImpl extends PublicRewardImpl {

    public RevokedPublicRewardImpl(com.extole.event.webhook.reward.RevokedPublicReward revokedRewardEvent) {
        super(revokedRewardEvent);
    }

    @Override
    public String getType() {
        return Type.REVOKED.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
