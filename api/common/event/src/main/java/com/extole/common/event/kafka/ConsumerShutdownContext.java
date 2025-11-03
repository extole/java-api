package com.extole.common.event.kafka;

public class ConsumerShutdownContext {

    private final boolean shutdownValue;
    private final String consumerGroupId;

    public ConsumerShutdownContext(boolean shutdownValue, String consumerGroupId) {
        this.shutdownValue = shutdownValue;
        this.consumerGroupId = consumerGroupId;
    }

    public boolean isShutdown() {
        return shutdownValue;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }
}
