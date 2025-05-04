package com.extole.api.impl.event.internal.send_reward;

import com.extole.api.event.internal.send_reward.SendRewardConsumerEvent;
import com.extole.api.impl.event.internal.reward.RewardConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class SendRewardConsumerEventImpl extends RewardConsumerEventImpl implements SendRewardConsumerEvent {

    private SendRewardConsumerEventImpl(
        com.extole.event.consumer.internal.send_reward.SendRewardConsumerEvent event,
        Person person) {
        super(event, person);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static SendRewardConsumerEvent newInstance(
        com.extole.event.consumer.internal.send_reward.SendRewardConsumerEvent event,
        Person person) {
        return new SendRewardConsumerEventImpl(event, person);
    }

}
