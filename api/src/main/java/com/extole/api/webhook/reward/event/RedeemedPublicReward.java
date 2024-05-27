package com.extole.api.webhook.reward.event;

import javax.annotation.Nullable;

public interface RedeemedPublicReward extends PublicReward {

    @Nullable
    String getPartnerEventId();

}
