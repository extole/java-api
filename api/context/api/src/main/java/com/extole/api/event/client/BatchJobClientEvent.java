package com.extole.api.event.client;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.batch.BatchJob;

@Schema
public interface BatchJobClientEvent extends ClientEvent {
    BatchJob getBatchJob();
}
