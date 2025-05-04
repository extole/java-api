package com.extole.api.impl.event.internal.reward_state;

import com.extole.api.event.internal.reward_state.RewardStateConsumerEvent;
import com.extole.api.impl.event.internal.reward.RewardConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class RewardStateConsumerEventImpl extends RewardConsumerEventImpl implements RewardStateConsumerEvent {

    private final String rewardState;

    private RewardStateConsumerEventImpl(
        com.extole.event.consumer.internal.reward_state.RewardStateConsumerEvent event,
        Person person) {
        super(event, person);

        this.rewardState = event.getRewardState().name();
    }

    @Override
    public String getRewardState() {
        return rewardState;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static RewardStateConsumerEvent newInstance(
        com.extole.event.consumer.internal.reward_state.RewardStateConsumerEvent event,
        Person person) {
        return new RewardStateConsumerEventImpl(event, person);
    }

}
