package com.extole.api.event.internal.reward_state;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.ReferralContext;
import com.extole.api.event.internal.InternalConsumerEvent;
import com.extole.api.event.internal.reward.RewardContext;

@Schema
public interface RewardStateConsumerEvent extends InternalConsumerEvent {

    RewardContext getReward();

    @Nullable
    ReferralContext getReferralContext();

    String getRewardState();

}
