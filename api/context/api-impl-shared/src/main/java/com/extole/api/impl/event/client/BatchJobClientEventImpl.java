package com.extole.api.impl.event.client;

import com.extole.api.batch.BatchJob;
import com.extole.api.event.client.BatchJobClientEvent;
import com.extole.api.impl.batch.BatchJobImpl;
import com.extole.common.lang.ToString;

public final class BatchJobClientEventImpl extends ClientEventImpl implements BatchJobClientEvent {

    private final BatchJob batchJob;

    private BatchJobClientEventImpl(com.extole.event.report.client.batch.BatchJobClientEvent clientEvent) {
        super(clientEvent);
        this.batchJob = new BatchJobImpl(clientEvent.getPojo());
    }

    public static BatchJobClientEventImpl
        newInstance(com.extole.event.report.client.batch.BatchJobClientEvent clientEvent) {
        return new BatchJobClientEventImpl(clientEvent);
    }

    @Override
    public BatchJob getBatchJob() {
        return batchJob;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
