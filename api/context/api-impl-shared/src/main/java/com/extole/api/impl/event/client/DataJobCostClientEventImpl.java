package com.extole.api.impl.event.client;

import com.extole.api.event.client.DataJobCostClientEvent;
import com.extole.api.impl.report.DataJobCostImpl;
import com.extole.api.report.DataJobCost;
import com.extole.common.lang.ToString;

public final class DataJobCostClientEventImpl extends ClientEventImpl implements DataJobCostClientEvent {

    private final DataJobCost dataJobCost;

    private DataJobCostClientEventImpl(com.extole.event.datajobcost.client.DataJobCostClientEvent clientEvent) {
        super(clientEvent);
        this.dataJobCost = new DataJobCostImpl(clientEvent.getPojo());
    }

    public static DataJobCostClientEventImpl
        newInstance(com.extole.event.datajobcost.client.DataJobCostClientEvent clientEvent) {
        return new DataJobCostClientEventImpl(clientEvent);
    }

    @Override
    public DataJobCost getDataJobCost() {
        return dataJobCost;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
