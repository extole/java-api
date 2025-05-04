package com.extole.api.impl.event.audience.membership.updated;

import com.extole.api.event.audience.membership.updated.AudienceMembershipUpdatedConsumerEvent;
import com.extole.api.impl.event.audience.membership.AudienceMembershipConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class AudienceMembershipUpdatedConsumerEventImpl extends AudienceMembershipConsumerEventImpl
    implements AudienceMembershipUpdatedConsumerEvent {

    private AudienceMembershipUpdatedConsumerEventImpl(
        com.extole.event.consumer.audience.membership.updated.AudienceMembershipUpdatedConsumerEvent event,
        Person person) {
        super(event, person);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static AudienceMembershipUpdatedConsumerEvent newInstance(
        com.extole.event.consumer.audience.membership.updated.AudienceMembershipUpdatedConsumerEvent event,
        Person person) {
        return new AudienceMembershipUpdatedConsumerEventImpl(event, person);
    }

}
