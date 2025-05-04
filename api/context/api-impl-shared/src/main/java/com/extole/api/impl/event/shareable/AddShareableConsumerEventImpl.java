package com.extole.api.impl.event.shareable;

import com.extole.api.event.shareable.AddShareableConsumerEvent;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.impl.person.ShareableImpl;
import com.extole.api.person.Person;
import com.extole.api.person.Shareable;
import com.extole.common.lang.ToString;

public final class AddShareableConsumerEventImpl extends ConsumerEventImpl implements AddShareableConsumerEvent {

    private final Shareable shareable;

    private AddShareableConsumerEventImpl(com.extole.event.consumer.shareable.AddShareableConsumerEvent event,
        Person person) {
        super(event, person);
        this.shareable = new ShareableImpl(event.getShareable());
    }

    @Override
    public Shareable getShareable() {
        return shareable;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static AddShareableConsumerEvent
        newInstance(com.extole.event.consumer.shareable.AddShareableConsumerEvent event, Person person) {
        return new AddShareableConsumerEventImpl(event, person);
    }
}
