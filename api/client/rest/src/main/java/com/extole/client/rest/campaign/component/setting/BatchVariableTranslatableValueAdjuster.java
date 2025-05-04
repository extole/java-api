package com.extole.client.rest.campaign.component.setting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ObjectMapperProvider;

public final class BatchVariableTranslatableValueAdjuster implements Consumer<ObjectNode> {
    private static final String FIELD_ABSOLUTE_PATH = "absolute_name";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DISPLAY_NAME = "display_name";
    private static final String FIELD_TYPE = "type";
    private static final BatchVariableTranslatableValueAdjuster INSTANCE = new BatchVariableTranslatableValueAdjuster();
    private static final Set<String> FIELDS_TO_BE_IGNORED = ImmutableSet.of(
        FIELD_ABSOLUTE_PATH,
        FIELD_NAME,
        FIELD_DISPLAY_NAME,
        FIELD_TYPE);
    private static final String HANDLEBARS_PREFIX = "handlebars@buildtime:";

    public static BatchVariableTranslatableValueAdjuster getInstance() {
        return INSTANCE;
    }

    private BatchVariableTranslatableValueAdjuster() {
    }

    @Override
    public void accept(ObjectNode objectNode) {
        adjustTranslatableValues(objectNode);
    }

    private void adjustTranslatableValues(ObjectNode variableObjectNode) {
        Map<String, JsonNode> changedFields = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = variableObjectNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();

            if (!FIELDS_TO_BE_IGNORED.contains(fieldName)) {
                if (containsRendertimeBars(fieldValue.asText())
                    || containsHandlebars(fieldValue.asText())) {

                    changedFields.put(fieldName,
                        ObjectMapperProvider.getInstance().getNodeFactory()
                            .textNode(HANDLEBARS_PREFIX + fieldValue.asText()));
                }
            }
        }

        for (Map.Entry<String, JsonNode> e : changedFields.entrySet()) {
            variableObjectNode.set(e.getKey(), e.getValue());
        }
    }

    private boolean containsRendertimeBars(String value) {
        return value.contains("{[") && value.contains("]}");
    }

    private boolean containsHandlebars(String value) {
        return value.contains("{{") && value.contains("}}");
    }
}
