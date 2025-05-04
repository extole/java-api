package com.extole.client.rest.blocks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BlockCreateRequest {
    private static final String FILTER_TYPE = "filter_type";
    private static final String LIST_TYPE = "list_type";
    private static final String VALUE = "value";

    private final String value;
    private final FilterType filterType;
    private final ListType listType;

    @JsonCreator
    public BlockCreateRequest(@JsonProperty(FILTER_TYPE) FilterType filterType,
        @JsonProperty(LIST_TYPE) ListType listType,
        @JsonProperty(VALUE) String value) {
        this.filterType = filterType;
        this.listType = listType;
        this.value = value;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty(FILTER_TYPE)
    public FilterType getFilterType() {
        return filterType;
    }

    @JsonProperty(LIST_TYPE)
    public ListType getListType() {
        return listType;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String value;
        private FilterType filterType;
        private ListType listType;

        private Builder() {
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public Builder withFilterType(FilterType filterType) {
            this.filterType = filterType;
            return this;
        }

        public Builder withListType(ListType listType) {
            this.listType = listType;
            return this;
        }

        public BlockCreateRequest build() {
            return new BlockCreateRequest(filterType, listType, value);
        }
    }
}
