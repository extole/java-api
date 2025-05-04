package com.extole.consumer.rest.me.shareable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class CreateMeShareableRequest {
    private static final String JSON_PROPERTY_PREFERRED_CODE_PREFIXES = "preferred_code_prefixes";
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_LABEL = "label";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";

    private final List<String> preferredCodePrefixes;
    private final String key;
    private final String label;
    private final ShareableContent content;
    private final Map<String, String> data;

    @JsonCreator
    public CreateMeShareableRequest(
        @JsonProperty(JSON_PROPERTY_LABEL) String label,
        @Nullable @JsonProperty(JSON_PROPERTY_PREFERRED_CODE_PREFIXES) List<String> preferredCodePrefixes,
        @Nullable @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableContent content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data) {
        this.label = label;
        this.preferredCodePrefixes = preferredCodePrefixes;
        this.key = key;
        this.content = content;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_PREFERRED_CODE_PREFIXES)
    public List<String> getPreferredCodePrefixes() {
        return preferredCodePrefixes;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableContent getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static CreateMeShareableRequestBuilder builder() {
        return new CreateMeShareableRequestBuilder();
    }

    public static final class CreateMeShareableRequestBuilder {
        private String label;
        private String key;
        private List<String> preferredCodes;
        private ShareableContent shareableContent;
        private Map<String, String> data;

        private CreateMeShareableRequestBuilder() {
        }

        public CreateMeShareableRequestBuilder withPreferredCodes(List<String> preferredCodes) {
            this.preferredCodes = preferredCodes;
            return this;
        }

        public CreateMeShareableRequestBuilder withShareableContent(ShareableContent shareableContent) {
            this.shareableContent = shareableContent;
            return this;
        }

        public CreateMeShareableRequestBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public CreateMeShareableRequestBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public CreateMeShareableRequestBuilder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public CreateMeShareableRequest build() {
            return new CreateMeShareableRequest(label, preferredCodes, key, shareableContent, data);
        }
    }
}
