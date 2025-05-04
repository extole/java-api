package com.extole.client.rest.campaign.component.setting;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ObjectMapperProvider;

public final class BatchVariableTypeBasedValueAdjuster implements Consumer<ObjectNode> {
    private static final String FIELD_ABSOLUTE_PATH = "absolute_name";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DISPLAY_NAME = "display_name";
    private static final String FIELD_TYPE = "type";
    private static final BatchVariableTypeBasedValueAdjuster INSTANCE = new BatchVariableTypeBasedValueAdjuster();
    private static final Set<String> VARIABLE_TYPES_AS_STRINGS = EnumSet.allOf(SettingType.class).stream()
        .map(Enum::name)
        .collect(toUnmodifiableSet());
    private static final Set<String> FIELDS_TO_BE_IGNORED = ImmutableSet.of(
        FIELD_ABSOLUTE_PATH,
        FIELD_NAME,
        FIELD_DISPLAY_NAME,
        FIELD_TYPE);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();

    public static BatchVariableTypeBasedValueAdjuster getInstance() {
        return INSTANCE;
    }

    private BatchVariableTypeBasedValueAdjuster() {
    }

    @Override
    public void accept(ObjectNode objectNode) {
        adjustValuesBasedOnVariableType(objectNode);
    }

    private void adjustValuesBasedOnVariableType(ObjectNode variableObjectNode) {
        JsonNode typeAsJsonNode = variableObjectNode.get(FIELD_TYPE);
        if (typeAsJsonNode != null && typeAsJsonNode.isTextual()
            && VARIABLE_TYPES_AS_STRINGS.contains(typeAsJsonNode.asText())) {

            SettingType settingType = SettingType.valueOf(typeAsJsonNode.textValue());

            Map<String, JsonNode> changedFields = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = variableObjectNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = fieldsIterator.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();

                if (!FIELDS_TO_BE_IGNORED.contains(fieldName)) {
                    if (!SettingType.INTEGER.equals(settingType) && fieldValue.isNumber()) {
                        changedFields.put(fieldName, OBJECT_MAPPER.getNodeFactory().textNode(fieldValue.asText()));
                    }
                    if (SettingType.JSON.equals(settingType) || SettingType.STRING.equals(settingType)) {
                        if (fieldValue instanceof ObjectNode || fieldValue instanceof ArrayNode) {
                            changedFields.put(fieldName,
                                OBJECT_MAPPER.getNodeFactory().textNode(serialize(fieldValue)));
                        }
                    }
                }
            }

            for (Map.Entry<String, JsonNode> e : changedFields.entrySet()) {
                variableObjectNode.set(e.getKey(), e.getValue());
            }
        }
    }

    private String serialize(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
