package com.extole.api.impl.event.reward;

import javax.annotation.Nullable;

import com.extole.api.webhook.reward.event.SentPublicReward;
import com.extole.common.lang.ToString;

public final class SentPublicRewardImpl extends PublicRewardImpl implements SentPublicReward {
    private final String email;

    public SentPublicRewardImpl(com.extole.event.webhook.reward.SentPublicReward sentRewardEvent) {
        super(sentRewardEvent);
        this.email = sentRewardEvent.getEmail().orElse(null);
    }

    @Override
    @Nullable
    public String getEmail() {
        return email;
    }

    @Override
    public String getType() {
        return Type.SENT.name();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
