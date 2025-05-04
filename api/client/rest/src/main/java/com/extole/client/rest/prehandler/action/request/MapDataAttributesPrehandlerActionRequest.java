package com.extole.client.rest.prehandler.action.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.action.DataAttributeMapping;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;

@Schema(description = "Action that modifies the event data based on data mappers attributes.")
public class MapDataAttributesPrehandlerActionRequest extends PrehandlerActionRequest {

    static final String TYPE = "MAP_DATA_ATTRIBUTES";
    private static final String JSON_DATA_MAPPERS = "data_attribute_mappings";

    private final List<DataAttributeMapping> dataAttributeMappings;

    @JsonCreator
    public MapDataAttributesPrehandlerActionRequest(
        @JsonProperty(JSON_DATA_MAPPERS) List<DataAttributeMapping> dataAttributeMappings) {
        super(PrehandlerActionType.MAP_DATA_ATTRIBUTES);
        this.dataAttributeMappings =
            ImmutableList.copyOf(dataAttributeMappings == null ? Collections.emptyList() : dataAttributeMappings);
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_DATA_MAPPERS)
    @Schema(required = true)
    public List<DataAttributeMapping> getDataAttributeMappings() {
        return dataAttributeMappings;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<DataAttributeMapping.Builder> dataAttributeMappingBuilders = new ArrayList<>();

        public DataAttributeMapping.Builder addDataAttributeMapping() {
            DataAttributeMapping.Builder dataAttributeMappingBuilder = DataAttributeMapping.builder();
            dataAttributeMappingBuilders.add(dataAttributeMappingBuilder);
            return dataAttributeMappingBuilder;
        }

        public MapDataAttributesPrehandlerActionRequest build() {
            List<DataAttributeMapping> attributeMappings = dataAttributeMappingBuilders.stream()
                .map(builder -> builder.build())
                .collect(Collectors.toList());

            return new MapDataAttributesPrehandlerActionRequest(attributeMappings);
        }
    }
}
