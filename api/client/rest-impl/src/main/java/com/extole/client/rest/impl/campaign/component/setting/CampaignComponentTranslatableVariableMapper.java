package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableValues;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.ValidEvaluatableModule;
import com.extole.evaluateable.handlebars.HandlebarsEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsRuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.model.entity.campaign.Variable;

@Component
public final class CampaignComponentTranslatableVariableMapper {
    private static final String HANDLEBARS_BUILDTIME = "handlebars@buildtime:";
    private static final String HANDLEBARS_RUNTIME = "handlebars@runtime:";
    public static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance()
        .copy()
        .registerModule(new ValidEvaluatableModule());

    public BatchComponentVariableValues mapToTranslatableVariableValue(
        BatchComponentVariableValues batchComponentVariableValues) {

        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values =
                batchComponentVariableValues.getValues().entrySet().stream()
                    .map(entry -> {
                        String serialized = serialize(entry.getValue());
                        if (StringUtils.startsWith(serialized, HANDLEBARS_BUILDTIME)) {
                            serialized = serialized.replace(HANDLEBARS_BUILDTIME, StringUtils.EMPTY);
                        }
                        if (StringUtils.startsWith(serialized, HANDLEBARS_RUNTIME)) {
                            serialized = serialized.replace(HANDLEBARS_RUNTIME, StringUtils.EMPTY);
                        }
                        return Pair.of(entry.getKey(), serialized);
                    })
                    .collect(Collectors.toUnmodifiableMap(pair -> pair.getLeft(),
                        pair -> Provided.nestedOptionalOf(pair.getRight())));

        return BatchComponentVariableValues.builder(batchComponentVariableValues)
            .withValues(values)
            .build();
    }

    public Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        convertToHandlebarsIfNeeded(
            Map<String,
                BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values,
            Variable currentVariable) {

        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> result =
                new LinkedHashMap<>();
        ObjectNode valuesAsObjectNode = OBJECT_MAPPER.convertValue(values,
            ObjectNode.class);

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = valuesAsObjectNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = resolveValue(entry.getValue(), currentVariable.getValues().get(fieldName));
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>> newValue =
                OBJECT_MAPPER
                    .convertValue(fieldValue, new TypeReference<>() {});
            result.put(fieldName, newValue);
        }

        return result;
    }

    private JsonNode resolveValue(JsonNode fieldValue,
        @Nullable BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>> existingFieldValue) {
        if (fieldValue instanceof NullNode) {
            return fieldValue;
        }

        if (existingFieldValue == null) {
            if (!fieldValue.isTextual()) {
                return fieldValue;
            } else {
                return convertToHandlebarsByValue(fieldValue);
            }
        }

        if (existingFieldValue instanceof Provided) {
            if (((Provided<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>) existingFieldValue)
                .getValue() instanceof HandlebarsRuntimeEvaluatable) {
                return JsonNodeFactory.instance.textNode("handlebars@runtime:" + fieldValue.asText());
            } else {
                return convertToHandlebarsByValue(fieldValue);
            }
        }

        if (existingFieldValue instanceof HandlebarsEvaluatable) {
            String serialized = serialize(existingFieldValue);
            if (serialized.startsWith("handlebars@buildtime:handlebars@runtime:")) {
                return JsonNodeFactory.instance.textNode("handlebars@buildtime:handlebars@runtime:"
                    + fieldValue.asText());
            }
            if (serialized.startsWith("handlebars@buildtime:")) {
                return JsonNodeFactory.instance.textNode("handlebars@buildtime:"
                    + fieldValue.asText());
            }
        }
        return convertToHandlebarsByValue(fieldValue);
    }

    private JsonNode convertToHandlebarsByValue(JsonNode fieldValue) {
        if (containsRendertimeBars(fieldValue.asText())) {
            return JsonNodeFactory.instance.textNode("handlebars@runtime:" + fieldValue.asText());
        } else if (containsHandlebars(fieldValue.asText())) {
            return JsonNodeFactory.instance.textNode("handlebars@buildtime:" + fieldValue.asText());
        } else {
            return fieldValue;
        }
    }

    private boolean containsRendertimeBars(String value) {
        return value.contains("{[") && value.contains("]}");
    }

    private boolean containsHandlebars(String value) {
        return value.contains("{{") && value.contains("}}");
    }

    private String serialize(Evaluatable<?, ?> evaluatable) {
        try {
            ObjectMapper objectMapper = OBJECT_MAPPER;
            return objectMapper.readValue(objectMapper.writeValueAsString(evaluatable), String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
