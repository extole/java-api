package com.extole.file.parser.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import com.extole.common.lang.ObjectMapperProvider;

final class GridConverter {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance()
        .copy()
        .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    private static final GridConverter INSTANCE = new GridConverter();

    private GridConverter() {
    }

    static GridConverter getInstance() {
        return INSTANCE;
    }

    <T> List<Map<String, String>> convertListToGrid(List<T> list) {
        List<Map<String, JsonNode>> jsonNodes = OBJECT_MAPPER.convertValue(list, new TypeReference<>() {});
        List<Map<String, String>> gridWithSerializedCells = new ArrayList<>(jsonNodes.size());
        for (Map<String, JsonNode> map : jsonNodes) {
            Map<String, String> newMap = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (value instanceof NullNode) {
                    newMap.put(key, null);
                } else {
                    newMap.put(key, serializeJsonNode(value));
                }
            });
            gridWithSerializedCells.add(newMap);
        }
        return Collections.unmodifiableList(gridWithSerializedCells);
    }

    private String serializeJsonNode(JsonNode jsonNode) {
        try {
            if (jsonNode.isTextual()) {
                return jsonNode.asText();
            }

            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
