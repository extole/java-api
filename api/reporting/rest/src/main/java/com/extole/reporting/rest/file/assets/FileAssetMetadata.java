package com.extole.reporting.rest.file.assets;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

public class FileAssetMetadata {

    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String FORMAT = "format";

    private final Optional<String> name;
    private final Optional<Set<String>> tags;
    private final Optional<String> format;

    public FileAssetMetadata(
        @Parameter(description = "Optional name, max length 255") @JsonProperty(NAME) Optional<String> name,
        @Parameter(description = "Optional tags, tag max length 255") @JsonProperty(TAGS) Optional<Set<String>> tags,
        @Parameter(
            description = "Optional file format, one of: CSV, PSV, JSON") @JsonProperty(FORMAT) Optional<
                String> format) {
        this.name = name;
        this.tags = tags;
        this.format = format;
    }

    @JsonProperty(NAME)
    public Optional<String> getName() {
        return name;
    }

    @JsonProperty(TAGS)
    public Optional<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(FORMAT)
    public Optional<String> getFormat() {
        return format;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<String> name = Optional.empty();
        private Optional<Set<String>> tags = Optional.empty();
        private Optional<String> format = Optional.empty();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = Optional.ofNullable(name);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Optional.ofNullable(tags);
            return this;
        }

        public Builder withFormat(String format) {
            this.format = Optional.ofNullable(format);
            return this;
        }

        public FileAssetMetadata build() {
            return new FileAssetMetadata(name, tags, format);
        }
    }
}
