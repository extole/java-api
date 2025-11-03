package com.extole.common.event;

public interface EventConsumer<T> {

    void startListener(EventListener<T> eventListener);

}
