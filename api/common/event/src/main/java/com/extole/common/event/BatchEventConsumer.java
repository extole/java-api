package com.extole.common.event;

public interface BatchEventConsumer<T> {

    void startListener(BatchEventListener<T> eventListener);

}
