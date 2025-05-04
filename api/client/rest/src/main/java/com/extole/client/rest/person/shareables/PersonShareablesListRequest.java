package com.extole.client.rest.person.shareables;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonShareablesListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_LABELS = "labels";
    private static final String PARAMETER_DATA_KEYS = "data_keys";
    private static final String PARAMETER_DATA_VALUES = "data_values";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";

    private final List<String> labels;
    private final List<String> dataKeys;
    private final List<String> dataValues;
    private final int offset;
    private final int limit;

    public PersonShareablesListRequest(
        @Parameter(description = "Optional label filter. " +
            "Will include shareables that match at least one of the labels.")
        @QueryParam(PARAMETER_LABELS) List<String> labels,
        @Parameter(description = "Optional data keys filter. " +
            "Will include shareables that match at least one of the data keys.")
        @QueryParam(PARAMETER_DATA_KEYS) List<String> dataKeys,
        @Parameter(description = "Optional filter for existence of specific data values. " +
            "Will include shareables that have at least one of the specified data name-value pair. " +
            "Valid format is name:value.")
        @QueryParam(PARAMETER_DATA_VALUES) List<String> dataValues,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".")
        @DefaultValue("" + DEFAULT_OFFSET)
        @QueryParam(PARAMETER_OFFSET) Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".")
        @DefaultValue("" + DEFAULT_LIMIT)
        @QueryParam(PARAMETER_LIMIT) Optional<Integer> limit) {
        this.labels = labels == null ? ImmutableList.of() : ImmutableList.copyOf(labels);
        this.dataKeys = dataKeys == null ? ImmutableList.of() : ImmutableList.copyOf(dataKeys);
        this.dataValues = dataValues == null ? ImmutableList.of() : ImmutableList.copyOf(dataValues);
        this.offset = offset.orElse(Integer.valueOf(DEFAULT_OFFSET)).intValue();
        this.limit = limit.orElse(Integer.valueOf(DEFAULT_LIMIT)).intValue();
    }

    @QueryParam(PARAMETER_LABELS)
    public List<String> getLabels() {
        return labels;
    }

    @QueryParam(PARAMETER_DATA_KEYS)
    public List<String> getDataKeys() {
        return dataKeys;
    }

    @QueryParam(PARAMETER_DATA_VALUES)
    public List<String> getDataValues() {
        return dataValues;
    }

    @QueryParam(PARAMETER_OFFSET)
    public int getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<String> labels = Lists.newArrayList();
        private final List<String> dataKeys = Lists.newArrayList();
        private final List<String> dataValues = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();

        private Builder() {
        }

        public Builder addLabel(String label) {
            this.labels.add(label);
            return this;
        }

        public Builder addDataKey(String dataKey) {
            this.dataKeys.add(dataKey);
            return this;
        }

        public Builder addDataValue(String dataKey, Object dataValue) {
            this.dataValues.add(dataKey + ":" + dataValue);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public PersonShareablesListRequest build() {
            return new PersonShareablesListRequest(labels, dataKeys, dataValues, offset, limit);
        }
    }
}
