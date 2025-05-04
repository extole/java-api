package com.extole.reporting.rest.audience.operation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AudienceOperationDataSourceResponse.TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ReportAudienceOperationDataSourceResponse.class,
        name = ReportAudienceOperationDataSourceResponse.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = FileAssetAudienceOperationDataSourceResponse.class,
        name = FileAssetAudienceOperationDataSourceResponse.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = PersonListAudienceOperationDataSourceResponse.class,
        name = PersonListAudienceOperationDataSourceResponse.DATA_SOURCE_TYPE),
    @JsonSubTypes.Type(value = ActionAudienceOperationDataSourceResponse.class,
        name = ActionAudienceOperationDataSourceResponse.DATA_SOURCE_TYPE)
})
public abstract class AudienceOperationDataSourceResponse {

    protected static final String TYPE = "type";

    private final AudienceOperationDataSourceType type;

    public AudienceOperationDataSourceResponse(@JsonProperty(TYPE) AudienceOperationDataSourceType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public AudienceOperationDataSourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
