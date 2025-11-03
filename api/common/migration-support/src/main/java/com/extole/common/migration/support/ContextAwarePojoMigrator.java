package com.extole.common.migration.support;

import java.io.Serializable;
import java.util.function.BiFunction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ContextAwarePojoMigrator<T extends ObjectNode, U, R> extends Serializable, BiFunction<T, U, R> {

    default String getSchemaVersionColumn() {
        return "schema_version";
    }

    int getInitialVersion();

    default boolean canMigrate(T entry) {
        JsonNode schemaNode = entry.get(getSchemaVersionColumn());
        if (schemaNode == null || !schemaNode.isValueNode()) {
            return false;
        }
        int schemaVersion = schemaNode.asInt();
        return schemaVersion == getInitialVersion();
    }

    R apply(T entry, U context);

}
