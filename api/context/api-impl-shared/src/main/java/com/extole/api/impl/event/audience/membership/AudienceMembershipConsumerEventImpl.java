package com.extole.api.impl.event.audience.membership;

import com.extole.api.event.audience.membership.Audience;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public abstract class AudienceMembershipConsumerEventImpl extends ConsumerEventImpl {

    private final Audience audience;

    protected AudienceMembershipConsumerEventImpl(
        com.extole.event.consumer.audience.membership.AudienceMembershipConsumerEvent event, Person person) {
        super(event, person);
        this.audience = new AudienceImpl(event.getClientContext().getClientId().getValue(),
            event.getAudience().getId().getValue(), event.getAudience().getName());
    }

    public Audience getAudience() {
        return audience;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
