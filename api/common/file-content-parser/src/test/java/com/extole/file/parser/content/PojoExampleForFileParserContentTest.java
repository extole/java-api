package com.extole.file.parser.content;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.extole.common.lang.ToString;

public class PojoExampleForFileParserContentTest<DATA_VALUE_TYPE> {

    private final String name;
    private final int status;
    @JsonIgnore
    private final Map<String, DATA_VALUE_TYPE> data = new HashMap<>();

    @JsonCreator
    public PojoExampleForFileParserContentTest(@JsonProperty("name") String name, @JsonProperty("status") int status) {
        this(name, status, Map.of());
    }

    public PojoExampleForFileParserContentTest(String name, int status, Map<String, DATA_VALUE_TYPE> data) {
        this.name = name;
        this.status = status;
        this.data.putAll(data);
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    @JsonAnyGetter
    public Map<String, DATA_VALUE_TYPE> getData() {
        return data;
    }

    @JsonAnySetter
    private void setDataProperty(String key, DATA_VALUE_TYPE value) {
        data.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PojoExampleForFileParserContentTest<?> that = (PojoExampleForFileParserContentTest<?>) o;

        return new EqualsBuilder().append(status, that.status)
            .append(name, that.name)
            .append(data, that.data)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(status).append(data).toHashCode();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
