package com.extole.api.event.shareable;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.person.Shareable;

public interface AddShareableConsumerEvent extends ConsumerEvent {

    Shareable getShareable();
}
