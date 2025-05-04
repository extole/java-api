package com.extole.api.impl.event.audience.membership.removed;

import com.extole.api.event.audience.membership.removed.AudienceMembershipRemovedConsumerEvent;
import com.extole.api.impl.event.audience.membership.AudienceMembershipConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class AudienceMembershipRemovedConsumerEventImpl extends AudienceMembershipConsumerEventImpl
    implements AudienceMembershipRemovedConsumerEvent {

    private AudienceMembershipRemovedConsumerEventImpl(
        com.extole.event.consumer.audience.membership.removed.AudienceMembershipRemovedConsumerEvent event,
        Person person) {
        super(event, person);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static AudienceMembershipRemovedConsumerEvent newInstance(
        com.extole.event.consumer.audience.membership.removed.AudienceMembershipRemovedConsumerEvent event,
        Person person) {
        return new AudienceMembershipRemovedConsumerEventImpl(event, person);
    }

}
