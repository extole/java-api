package com.extole.api.event.client;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.report.DataJobCost;

@Schema
public interface DataJobCostClientEvent extends ClientEvent {
    DataJobCost getDataJobCost();
}
