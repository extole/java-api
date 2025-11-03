package com.extole.util.tag;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class Tag {

    public static final String TYPE_INTERNAL = "internal";
    public static final String TYPE_EXTOLE = "extole";

    private static final String PREFIX_SEPARATOR = ":";
    private static final String VALUE_SEPARATOR = "=";

    private final String name;
    private final Optional<String> type;
    private final Optional<String> value;

    private Tag(String name, @Nullable String value, @Nullable String type) {
        this.name = name;
        this.type = Optional.ofNullable(type);
        this.value = Optional.ofNullable(value);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getType() {
        return type;
    }

    public Optional<String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder tagStringBuilder = new StringBuilder(name);
        type.ifPresent(type -> tagStringBuilder.insert(0, type + PREFIX_SEPARATOR));
        value.ifPresent(value -> tagStringBuilder.append(VALUE_SEPARATOR).append(value));
        return tagStringBuilder.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Tag tag = (Tag) other;
        return name.equals(tag.name) && type.equals(tag.type) && value.equals(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }

    public static Builder create(String name) {
        return new Builder().withName(name);
    }

    public static Builder createInternal(String name) {
        return new Builder().withName(name).withType(TYPE_INTERNAL);
    }

    public static Builder createExtole(String name) {
        return new Builder().withName(name).withType(TYPE_EXTOLE);
    }

    public static final class Builder {
        private String name;
        private String value;
        private String type;

        public Builder withName(String name) {
            validateName(name);
            this.name = name;
            return this;
        }

        public Builder withValue(String value) {
            validateValue(value);
            this.value = value;
            return this;
        }

        public Builder withType(String type) {
            validateType(type);
            this.type = type;
            return this;
        }

        public Tag build() {
            validate();
            return new Tag(name, value, type);
        }

        public String buildAsString() {
            validate();
            return build().toString();
        }

        private void validate() {
            if (name == null) {
                throw new TagBuildRuntimeException("Cannot create tag without name");
            }
        }

        private static void validateName(String name) {
            if (StringUtils.trimToNull(name) == null) {
                throw new TagBuildRuntimeException("Name cannot be blank, empty or null. Name: \"" + name + "\"");
            }
        }

        private static void validateValue(String value) {
            if (StringUtils.trimToNull(value) == null) {
                throw new TagBuildRuntimeException("Value cannot be blank, empty or null. Value: \"" + value + "\"");
            }
        }

        private static void validateType(String type) {
            if (StringUtils.trimToNull(type) == null) {
                throw new TagBuildRuntimeException("Type cannot be blank, empty or null. Type: \"" + type + "\"");
            }
        }

        private static class TagBuildRuntimeException extends RuntimeException {
            TagBuildRuntimeException(String message) {
                super(message);
            }
        }
    }
}
