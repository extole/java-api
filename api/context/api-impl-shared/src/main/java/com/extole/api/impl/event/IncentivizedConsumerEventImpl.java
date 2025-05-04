package com.extole.api.impl.event;

import com.extole.api.event.IncentivizedConsumerEvent;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class IncentivizedConsumerEventImpl extends ConsumerEventImpl implements IncentivizedConsumerEvent {

    private final String name;
    private final String quality;

    private IncentivizedConsumerEventImpl(com.extole.event.consumer.incentivized.IncentivizedConsumerEvent event,
        Person person) {
        super(event, person);
        this.name = event.getName();
        this.quality = event.getQuality().name();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static IncentivizedConsumerEvent newInstance(
        com.extole.event.consumer.incentivized.IncentivizedConsumerEvent event, Person person) {
        return new IncentivizedConsumerEventImpl(event, person);
    }

}
