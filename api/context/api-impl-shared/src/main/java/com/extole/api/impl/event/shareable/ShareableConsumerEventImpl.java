package com.extole.api.impl.event.shareable;

import com.extole.api.event.shareable.ShareableConsumerEvent;
import com.extole.api.event.shareable.ShareableWithLink;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class ShareableConsumerEventImpl extends ConsumerEventImpl implements ShareableConsumerEvent {

    private final boolean isNew;
    private final ShareableWithLink shareable;

    private ShareableConsumerEventImpl(com.extole.event.consumer.shareable.ShareableConsumerEvent event,
        Person person) {
        super(event, person);
        this.isNew = event.isNew().booleanValue();
        this.shareable = new ShareableWithLinkImpl(event.getShareable());
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public ShareableWithLink getShareable() {
        return shareable;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static ShareableConsumerEvent newInstance(com.extole.event.consumer.shareable.ShareableConsumerEvent event,
        Person person) {
        return new ShareableConsumerEventImpl(event, person);
    }

}
