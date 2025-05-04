package com.extole.api.impl.event.audience.membership.created;

import com.extole.api.event.audience.membership.created.AudienceMembershipCreatedConsumerEvent;
import com.extole.api.impl.event.audience.membership.AudienceMembershipConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class AudienceMembershipCreatedConsumerEventImpl
    extends AudienceMembershipConsumerEventImpl implements AudienceMembershipCreatedConsumerEvent {

    private AudienceMembershipCreatedConsumerEventImpl(
        com.extole.event.consumer.audience.membership.created.AudienceMembershipCreatedConsumerEvent event,
        Person person) {
        super(event, person);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static AudienceMembershipCreatedConsumerEvent newInstance(
        com.extole.event.consumer.audience.membership.created.AudienceMembershipCreatedConsumerEvent event,
        Person person) {
        return new AudienceMembershipCreatedConsumerEventImpl(event, person);
    }

}
