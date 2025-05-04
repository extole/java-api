package com.extole.api.event.shareable;

import com.extole.api.event.ConsumerEvent;

public interface ShareableConsumerEvent extends ConsumerEvent {

    boolean isNew();

    ShareableWithLink getShareable();

}
