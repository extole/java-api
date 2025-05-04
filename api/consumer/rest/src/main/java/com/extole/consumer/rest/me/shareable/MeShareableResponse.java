package com.extole.consumer.rest.me.shareable;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class MeShareableResponse {
    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_LABEL = "label";
    private static final String JSON_PROPERTY_LINK = "link";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";

    private final String code;
    private final String key;
    private final String label;
    private final String link;
    private final ShareableContent content;
    private final Map<String, String> data;

    @JsonCreator
    public MeShareableResponse(
        @JsonProperty(JSON_PROPERTY_CODE) String code,
        @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label,
        @JsonProperty(JSON_PROPERTY_LINK) String link,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableContent content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data) {
        this.code = code;
        this.key = key;
        this.label = label;
        this.link = link;
        this.content = content;
        this.data = data;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LINK)
    public String getLink() {
        return link;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableContent getContent() {
        return content;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
