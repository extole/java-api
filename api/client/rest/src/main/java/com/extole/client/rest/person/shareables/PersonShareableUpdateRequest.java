package com.extole.client.rest.person.shareables;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public final class PersonShareableUpdateRequest {
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_LABEL = "label";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";
    private static final String JSON_PROPERTY_PROGRAM_URL = "program_url";

    private final Omissible<Optional<String>> label;
    private final Omissible<Optional<String>> key;
    private final Omissible<PersonShareableContentRequest> content;
    private final Omissible<Map<String, String>> data;
    private final Omissible<String> programUrl;

    @JsonCreator
    private PersonShareableUpdateRequest(
        @JsonProperty(JSON_PROPERTY_LABEL) Omissible<Optional<String>> label,
        @JsonProperty(JSON_PROPERTY_KEY) Omissible<Optional<String>> key,
        @JsonProperty(JSON_PROPERTY_CONTENT) Omissible<PersonShareableContentRequest> content,
        @JsonProperty(JSON_PROPERTY_DATA) Omissible<Map<String, String>> data,
        @JsonProperty(JSON_PROPERTY_PROGRAM_URL) Omissible<String> programUrl) {
        this.label = label;
        this.key = key;
        this.content = content;
        this.data = data;
        this.programUrl = programUrl;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public Omissible<Optional<String>> getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_KEY)
    public Omissible<Optional<String>> getKey() {
        return key;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT)
    public Omissible<PersonShareableContentRequest> getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    public Omissible<Map<String, String>> getData() {
        return data;
    }

    @JsonProperty(JSON_PROPERTY_PROGRAM_URL)
    public Omissible<String> getProgramUrl() {
        return programUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Optional<String>> label = Omissible.omitted();
        private Omissible<Optional<String>> key = Omissible.omitted();
        private Omissible<PersonShareableContentRequest> content = Omissible.omitted();
        private Omissible<Map<String, String>> data = Omissible.omitted();
        private Omissible<String> programUrl = Omissible.omitted();

        private Builder() {
        }

        public Builder withLabel(String label) {
            this.label = Omissible.of(Optional.ofNullable(label));
            return this;
        }

        public Builder withKey(String key) {
            this.key = Omissible.of(Optional.ofNullable(key));
            return this;
        }

        public Builder withContent(PersonShareableContentRequest content) {
            this.content = Omissible.of(content);
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withProgramUrl(String programUrl) {
            this.programUrl = Omissible.of(programUrl);
            return this;
        }

        public PersonShareableUpdateRequest build() {
            return new PersonShareableUpdateRequest(label, key, content, data, programUrl);
        }
    }
}
