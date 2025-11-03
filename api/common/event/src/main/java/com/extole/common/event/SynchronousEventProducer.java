package com.extole.common.event;

public interface SynchronousEventProducer<T> {

    SynchronousEventBuilder<T> createSynchronousBuilder();

}
