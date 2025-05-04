package com.extole.consumer.rest.me.shareable.v5;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateMeShareableV5Request {
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_LABEL = "label";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";

    private final String key;
    private final String label;
    private final ShareableV5Content content;
    private final Map<String, String> data;

    @JsonCreator
    public UpdateMeShareableV5Request(
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label,
        @Nullable @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV5Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data) {
        this.label = label;
        this.key = key;
        this.content = content;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV5Content getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

}
