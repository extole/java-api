package com.extole.common.event;

public interface EventProducer<T> {

    EventBuilder<T> createBuilder();

}
