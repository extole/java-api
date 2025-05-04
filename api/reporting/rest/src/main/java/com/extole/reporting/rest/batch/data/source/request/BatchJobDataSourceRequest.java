package com.extole.reporting.rest.batch.data.source.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BatchJobDataSourceRequest.TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ReportBatchJobDataSourceRequest.class,
        name = ReportBatchJobDataSourceRequest.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = AudienceListBatchJobDataSourceRequest.class,
        name = AudienceListBatchJobDataSourceRequest.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = FileAssetBatchJobDataSourceRequest.class,
        name = FileAssetBatchJobDataSourceRequest.DATA_SOURCE_TYPE),
})
public abstract class BatchJobDataSourceRequest {

    protected static final String TYPE = "type";

    private final BatchJobDataSourceType type;

    public BatchJobDataSourceRequest(@JsonProperty(TYPE) BatchJobDataSourceType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public BatchJobDataSourceType getType() {
        return type;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
