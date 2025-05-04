package com.extole.client.rest.campaign.component.anchor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class AnchorDetailsResponse {

    private static final String JSON_SOURCE_ELEMENT_ID = "source_element_id";
    private static final String JSON_SOURCE_ELEMENT_TYPE = "source_element_type";

    private final Id<?> sourceElementId;
    private final SourceElementType sourceElementType;

    @JsonCreator
    public AnchorDetailsResponse(@JsonProperty(JSON_SOURCE_ELEMENT_ID) Id<?> sourceElementId,
        @JsonProperty(JSON_SOURCE_ELEMENT_TYPE) SourceElementType sourceElementType) {
        this.sourceElementId = sourceElementId;
        this.sourceElementType = sourceElementType;
    }

    @JsonProperty(JSON_SOURCE_ELEMENT_ID)
    public Id<?> getSourceElementId() {
        return sourceElementId;
    }

    @JsonProperty(JSON_SOURCE_ELEMENT_TYPE)
    public SourceElementType getSourceElementType() {
        return sourceElementType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
