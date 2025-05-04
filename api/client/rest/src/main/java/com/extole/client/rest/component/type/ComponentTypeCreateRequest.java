package com.extole.client.rest.component.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@Schema
public class ComponentTypeCreateRequest {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String SCHEMA = "schema";
    private static final String PARENT = "parent";

    private final String name;
    private final Omissible<String> displayName;
    private final String schema;
    private final Omissible<String> parent;

    public ComponentTypeCreateRequest(@JsonProperty(NAME) String name,
        @JsonProperty(DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(SCHEMA) String schema,
        @JsonProperty(PARENT) Omissible<String> parent) {
        this.name = name;
        this.displayName = displayName;
        this.schema = schema;
        this.parent = parent;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DISPLAY_NAME)
    public Omissible<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(SCHEMA)
    public String getSchema() {
        return schema;
    }

    @JsonProperty(PARENT)
    public Omissible<String> getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private Omissible<String> displayName = Omissible.omitted();
        private String schema;
        private Omissible<String> parent = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Omissible.of(displayName);
            return this;
        }

        public Builder withSchema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder withParent(String parent) {
            this.parent = Omissible.of(parent);
            return this;
        }

        public ComponentTypeCreateRequest build() {
            return new ComponentTypeCreateRequest(name, displayName, schema, parent);
        }

    }

}
