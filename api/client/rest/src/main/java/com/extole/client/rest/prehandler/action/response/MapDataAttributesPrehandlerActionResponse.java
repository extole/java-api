package com.extole.client.rest.prehandler.action.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.action.DataAttributeMapping;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.common.lang.ToString;

@Schema(description = "Action represented by data mappers that can change the data of the event.")
public class MapDataAttributesPrehandlerActionResponse extends PrehandlerActionResponse {

    static final String TYPE = "MAP_DATA_ATTRIBUTES";
    private static final String JSON_DATA_MAPPERS = "data_attribute_mappings";

    private final List<DataAttributeMapping> dataAttributeMappings;

    @JsonCreator
    public MapDataAttributesPrehandlerActionResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_DATA_MAPPERS) List<DataAttributeMapping> dataAttributeMappings) {
        super(id, PrehandlerActionType.MAP_DATA_ATTRIBUTES);
        this.dataAttributeMappings = ImmutableList.copyOf(dataAttributeMappings);
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_DATA_MAPPERS)
    public List<DataAttributeMapping> getDataAttributeMappings() {
        return dataAttributeMappings;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
