package com.extole.client.rest.promotion;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class PromotionLinkUpdateRequest {
    private static final String PROPERTY_KEY = "key";
    private static final String PROPERTY_PROGRAM_URL = "program_url";
    private static final String PROPERTY_CONTENT = "content";
    private static final String PROPERTY_DATA = "data";
    private static final String PROPERTY_LABEL = "label";
    private static final String PROPERTY_DESCRIPTION = "description";

    private final Omissible<String> key;
    private final Omissible<String> programUrl;
    private final Omissible<Optional<PromotionLinkContentRequest>> content;
    private final Omissible<Optional<Map<String, String>>> data;
    private final Omissible<Optional<String>> label;
    private final Omissible<Optional<String>> description;

    @JsonCreator
    PromotionLinkUpdateRequest(
        @JsonProperty(PROPERTY_KEY) Omissible<String> key,
        @JsonProperty(PROPERTY_PROGRAM_URL) Omissible<String> programUrl,
        @JsonProperty(PROPERTY_CONTENT) Omissible<Optional<PromotionLinkContentRequest>> content,
        @JsonProperty(PROPERTY_DATA) Omissible<Optional<Map<String, String>>> data,
        @JsonProperty(PROPERTY_LABEL) Omissible<Optional<String>> label,
        @JsonProperty(PROPERTY_DESCRIPTION) Omissible<Optional<String>> description) {
        this.key = key;
        this.programUrl = programUrl;
        this.content = content;
        this.data = data;
        this.label = label;
        this.description = description;
    }

    @JsonProperty(PROPERTY_KEY)
    public Omissible<String> getKey() {
        return key;
    }

    @JsonProperty(PROPERTY_PROGRAM_URL)
    public Omissible<String> getProgramUrl() {
        return programUrl;
    }

    @JsonProperty(PROPERTY_CONTENT)
    public Omissible<Optional<PromotionLinkContentRequest>> getContent() {
        return content;
    }

    @JsonProperty(PROPERTY_DATA)
    public Omissible<Optional<Map<String, String>>> getData() {
        return data;
    }

    @JsonProperty(PROPERTY_LABEL)
    public Omissible<Optional<String>> getLabel() {
        return label;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<String> key = Omissible.omitted();
        private Omissible<String> programUrl = Omissible.omitted();
        private Omissible<Optional<PromotionLinkContentRequest>> content = Omissible.omitted();
        private Omissible<Optional<Map<String, String>>> data = Omissible.omitted();
        private Omissible<Optional<String>> label = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();

        private Builder() {
        }

        public Builder withKey(String key) {
            this.key = Omissible.of(key);
            return this;
        }

        public Builder withProgramUrl(String programUrl) {
            this.programUrl = Omissible.of(programUrl);
            return this;
        }

        public Builder withContent(Optional<PromotionLinkContentRequest> content) {
            this.content = Omissible.of(content);
            return this;
        }

        public Builder withData(Optional<Map<String, String>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withLabel(Optional<String> label) {
            this.label = Omissible.of(label);
            return this;
        }

        public Builder withDescription(Optional<String> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public PromotionLinkUpdateRequest build() {
            return new PromotionLinkUpdateRequest(key, programUrl, content, data, label, description);
        }

    }

}
