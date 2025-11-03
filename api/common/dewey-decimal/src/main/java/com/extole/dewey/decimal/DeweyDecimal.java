package com.extole.dewey.decimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

@Schema
@JsonDeserialize(using = DeweyDecimalDeserializer.class)
@JsonSerialize(using = DeweyDecimalSerializer.class)
public final class DeweyDecimal implements Comparable<DeweyDecimal> {

    private final org.apache.tools.ant.util.DeweyDecimal value;

    private DeweyDecimal(String stringValue) {
        Preconditions.checkArgument(StringUtils.isNotBlank(stringValue), "Blank value not allowed");
        this.value = new org.apache.tools.ant.util.DeweyDecimal(stringValue);
    }

    public int getSize() {
        return value.getSize();
    }

    public int get(int index) {
        return value.get(index);
    }

    public boolean isLessThan(DeweyDecimal other) {
        return value.isLessThan(other.value);
    }

    public boolean isLessThanOrEqual(DeweyDecimal other) {
        return value.isLessThanOrEqual(other.value);
    }

    public boolean isGreaterThan(DeweyDecimal other) {
        return value.isGreaterThan(other.value);
    }

    public boolean isGreaterThanOrEqual(DeweyDecimal other) {
        return value.isGreaterThanOrEqual(other.value);
    }

    @Override
    public int compareTo(DeweyDecimal other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != DeweyDecimal.class) {
            return false;
        }

        return value.equals(((DeweyDecimal) other).value);
    }

    public static DeweyDecimal valueOf(String value) {
        return new DeweyDecimal(value);
    }

}
