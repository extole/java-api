package com.extole.client.rest.blocks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class BlockCheckRequest {
    private static final String LIST_TYPE = "list_type";
    private static final String VALUE = "value";

    private final String value;
    private final ListType listType;

    @JsonCreator
    private BlockCheckRequest(
        @JsonProperty(LIST_TYPE) ListType listType,
        @JsonProperty(VALUE) String value) {
        this.listType = listType;
        this.value = value;
    }

    @JsonProperty(LIST_TYPE)
    public ListType getListType() {
        return listType;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ListType listType;
        private String value;

        public Builder withListType(ListType listType) {
            this.listType = listType;
            return this;
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public BlockCheckRequest build() {
            return new BlockCheckRequest(listType, value);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
