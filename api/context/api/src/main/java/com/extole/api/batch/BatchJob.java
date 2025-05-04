package com.extole.api.batch;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.batch.column.BatchJobColumn;

@Schema
public interface BatchJob {

    enum Status {
        PENDING_RETRY, QUEUED, PENDING, IN_PROGRESS, DISPATCHING, DONE, FAILED, CANCELED, EXPIRED
    }

    String getBatchJobId();

    String getName();

    @Nullable
    String getEventName();

    String getDefaultEventName();

    String[] getTags();

    Map<String, String> getEventData();

    String[] getEventColumns();

    BatchJobColumn[] getColumns();

    String getCreatedDate();

    String getUpdatedDate();

    String getClientId();

    String getUserId();

    Status getStatus();

    BatchJobResult getResult();
}
