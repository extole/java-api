package com.extole.client.rest.person.shareables;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public final class PersonShareableCreateRequest {
    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_LABEL = "label";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";
    private static final String PROPERTY_PROGRAM_URL = "program_url";

    private final String label;
    private final Omissible<String> code;
    private final Omissible<String> key;
    private final Omissible<PersonShareableContentRequest> content;
    private final Omissible<Map<String, String>> data;
    private final Omissible<String> programUrl;

    @JsonCreator
    private PersonShareableCreateRequest(
        @JsonProperty(JSON_PROPERTY_LABEL) String label,
        @JsonProperty(JSON_PROPERTY_CODE) Omissible<String> code,
        @JsonProperty(JSON_PROPERTY_KEY) Omissible<String> key,
        @JsonProperty(JSON_PROPERTY_CONTENT) Omissible<PersonShareableContentRequest> content,
        @JsonProperty(JSON_PROPERTY_DATA) Omissible<Map<String, String>> data,
        @JsonProperty(PROPERTY_PROGRAM_URL) Omissible<String> programUrl) {
        this.label = label;
        this.code = code;
        this.key = key;
        this.content = content;
        this.data = data;
        this.programUrl = programUrl;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    public Omissible<String> getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_KEY)
    public Omissible<String> getKey() {
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

    @JsonProperty(PROPERTY_PROGRAM_URL)
    public Omissible<String> getProgramUrl() {
        return programUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String label;
        private Omissible<String> code = Omissible.omitted();
        private Omissible<String> key = Omissible.omitted();
        private Omissible<PersonShareableContentRequest> content = Omissible.omitted();
        private Omissible<Map<String, String>> data = Omissible.omitted();
        private Omissible<String> programUrl = Omissible.omitted();

        private Builder() {
        }

        public Builder withLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder withCode(String code) {
            this.code = Omissible.of(code);
            return this;
        }

        public Builder withKey(String key) {
            this.key = Omissible.of(key);
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

        public PersonShareableCreateRequest build() {
            return new PersonShareableCreateRequest(label, code, key, content, data, programUrl);
        }
    }
}
