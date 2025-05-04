package com.extole.reporting.rest.batch.data.source.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BatchJobDataSourceResponse.TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ReportBatchJobDataSourceResponse.class,
        name = ReportBatchJobDataSourceResponse.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = AudienceListBatchJobDataSourceResponse.class,
        name = AudienceListBatchJobDataSourceResponse.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = FileAssetBatchJobDataSourceResponse.class,
        name = FileAssetBatchJobDataSourceResponse.DATA_SOURCE_TYPE),
})
public abstract class BatchJobDataSourceResponse {

    protected static final String TYPE = "type";
    protected static final String ID = "id";

    private final String id;
    private final BatchJobDataSourceType type;

    public BatchJobDataSourceResponse(@JsonProperty(ID) String id,
        @JsonProperty(TYPE) BatchJobDataSourceType type) {
        this.id = id;
        this.type = type;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
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
