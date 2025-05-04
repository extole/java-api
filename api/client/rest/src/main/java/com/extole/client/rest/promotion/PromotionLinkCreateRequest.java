package com.extole.client.rest.promotion;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class PromotionLinkCreateRequest {
    private static final String PROPERTY_CODE = "code";
    private static final String PROPERTY_KEY = "key";
    private static final String PROPERTY_PROGRAM_URL = "program_url";
    private static final String PROPERTY_CONTENT = "content";
    private static final String PROPERTY_DATA = "data";
    private static final String PROPERTY_LABEL = "label";
    private static final String PROPERTY_DESCRIPTION = "description";

    private final String code;
    private final String key;
    private final String programUrl;
    private final Omissible<PromotionLinkContentRequest> content;
    private final Omissible<Map<String, String>> data;
    private final Omissible<String> label;
    private final Omissible<String> description;

    @JsonCreator
    PromotionLinkCreateRequest(
        @JsonProperty(PROPERTY_CODE) String code,
        @JsonProperty(PROPERTY_KEY) String key,
        @JsonProperty(PROPERTY_PROGRAM_URL) String programUrl,
        @JsonProperty(PROPERTY_CONTENT) Omissible<PromotionLinkContentRequest> content,
        @JsonProperty(PROPERTY_DATA) Omissible<Map<String, String>> data,
        @JsonProperty(PROPERTY_LABEL) Omissible<String> label,
        @JsonProperty(PROPERTY_DESCRIPTION) Omissible<String> description) {
        this.code = code;
        this.key = key;
        this.programUrl = programUrl;
        this.content = content;
        this.data = data;
        this.label = label;
        this.description = description;
    }

    @JsonProperty(PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @Nullable
    @JsonProperty(PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @Nullable
    @JsonProperty(PROPERTY_PROGRAM_URL)
    public String getProgramUrl() {
        return programUrl;
    }

    @JsonProperty(PROPERTY_CONTENT)
    public Omissible<PromotionLinkContentRequest> getContent() {
        return content;
    }

    @JsonProperty(PROPERTY_DATA)
    public Omissible<Map<String, String>> getData() {
        return data;
    }

    @JsonProperty(PROPERTY_LABEL)
    public Omissible<String> getLabel() {
        return label;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Omissible<String> getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String code;
        private String key;
        private String programUrl;
        private Omissible<PromotionLinkContentRequest> content = Omissible.omitted();
        private Omissible<Map<String, String>> data = Omissible.omitted();
        private Omissible<String> label = Omissible.omitted();
        private Omissible<String> description = Omissible.omitted();

        private Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withKey(String key) {
            this.key = key;
            return this;
        }

        public Builder withProgramUrl(String programUrl) {
            this.programUrl = programUrl;
            return this;
        }

        public Builder withContent(PromotionLinkContentRequest content) {
            this.content = Omissible.of(content);
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withLabel(String label) {
            this.label = Omissible.of(label);
            return this;
        }

        public Builder withDescription(Omissible<String> description) {
            this.description = description;
            return this;
        }

        public PromotionLinkCreateRequest build() {
            return new PromotionLinkCreateRequest(code, key, programUrl, content, data, label, description);
        }
    }
}
