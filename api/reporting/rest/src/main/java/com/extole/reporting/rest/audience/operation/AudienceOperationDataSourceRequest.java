package com.extole.reporting.rest.audience.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceRequest;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AudienceOperationDataSourceRequest.TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ReportAudienceOperationDataSourceRequest.class,
        name = ReportAudienceOperationDataSourceRequest.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = FileAssetAudienceOperationDataSourceRequest.class,
        name = FileAssetAudienceOperationDataSourceRequest.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = PersonListAudienceOperationDataSourceRequest.class,
        name = PersonListAudienceOperationDataSourceRequest.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = ActionAudienceOperationDataSourceRequest.class,
        name = ActionAudienceOperationDataSourceRequest.DATA_SOURCE_TYPE)
})
public abstract class AudienceOperationDataSourceRequest {

    protected static final String TYPE = "type";

    private final AudienceOperationDataSourceType type;

    public AudienceOperationDataSourceRequest(@JsonProperty(TYPE) AudienceOperationDataSourceType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public AudienceOperationDataSourceType getType() {
        return type;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }

}
