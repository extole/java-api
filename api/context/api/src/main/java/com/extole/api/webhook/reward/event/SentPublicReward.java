package com.extole.api.webhook.reward.event;

import javax.annotation.Nullable;

public interface SentPublicReward extends PublicReward {

    @Nullable
    String getEmail();

}
