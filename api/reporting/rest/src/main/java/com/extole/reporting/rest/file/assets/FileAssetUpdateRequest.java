package com.extole.reporting.rest.file.assets;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class FileAssetUpdateRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_TAGS = "tags";

    private final Omissible<String> name;
    private final Omissible<Set<String>> tags;

    public FileAssetUpdateRequest(
        @JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_TAGS) Omissible<Set<String>> tags) {
        this.name = name;
        this.tags = tags;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<String> name = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public FileAssetUpdateRequest build() {
            return new FileAssetUpdateRequest(name, tags);
        }
    }
}
