package com.extole.api.impl.batch;

import java.util.Map;

import javax.annotation.Nullable;

import com.extole.api.batch.BatchJob;
import com.extole.api.batch.BatchJobResult;
import com.extole.api.batch.column.BatchJobColumn;
import com.extole.api.impl.batch.column.BatchJobColumnMapper;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;

public class BatchJobImpl implements BatchJob {

    private final String batchJobId;
    private final String name;
    private final String eventName;
    private final String defaultEventName;
    private final String[] tags;
    private final Map<String, String> eventData;
    private final String[] eventColumns;
    private final BatchJobColumn[] columns;
    private final String createdDate;
    private final String updatedDate;
    private final String clientId;
    private final String userId;
    private final Status status;
    private final BatchJobResult result;

    public BatchJobImpl(com.extole.reporting.entity.batch.BatchJob batchJob) {
        this.batchJobId = batchJob.getId().getValue();
        this.name = batchJob.getName();
        this.eventName = batchJob.getEventName().orElse(null);
        this.defaultEventName = batchJob.getDefaultEventName();
        this.tags = batchJob.getTags().toArray(new String[] {});
        this.eventData = batchJob.getEventData();
        this.eventColumns = batchJob.getEventColumns().toArray(new String[] {});
        BatchJobColumnMapper mapper = new BatchJobColumnMapper();
        this.columns = batchJob.getColumns().stream()
            .map(batchJobColumn -> mapper.mapToApi(batchJobColumn))
            .toArray(BatchJobColumn[]::new);
        this.createdDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(batchJob.getCreatedDate());
        this.updatedDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(batchJob.getUpdatedDate());
        this.clientId = batchJob.getClientId().getValue();
        this.userId = batchJob.getUserId().getValue();
        this.status = Status.valueOf(batchJob.getStatus().name());
        this.result = new BatchJobResultImpl(batchJob.getResult());
    }

    @Override
    public String getBatchJobId() {
        return batchJobId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getDefaultEventName() {
        return defaultEventName;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public Map<String, String> getEventData() {
        return eventData;
    }

    @Override
    public String[] getEventColumns() {
        return eventColumns;
    }

    @Override
    public BatchJobColumn[] getColumns() {
        return columns;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public BatchJobResult getResult() {
        return result;
    }
}
