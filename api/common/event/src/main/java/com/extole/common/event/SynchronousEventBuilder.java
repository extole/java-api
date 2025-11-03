package com.extole.common.event;

import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.producer.KafkaSendEventFailureException;

public interface SynchronousEventBuilder<T> {

    T sendSynchronously() throws InterruptedException, KafkaSendEventFailureException, EventTooLargeException;

}
