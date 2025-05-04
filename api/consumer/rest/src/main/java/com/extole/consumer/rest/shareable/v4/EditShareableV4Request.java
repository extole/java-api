package com.extole.consumer.rest.shareable.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@Deprecated // TODO remove ENG-10127
public class EditShareableV4Request {

    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_TARGET_URL = "target_url";
    private static final String JSON_PROPERTY_CONTENT = "content";

    private final String key;
    private final String targetUrl;
    private final ShareableV4Content content;

    public EditShareableV4Request(@Nullable @JsonProperty(JSON_PROPERTY_KEY) String key,
        @JsonProperty(JSON_PROPERTY_TARGET_URL) String targetUrl,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV4Content content) {
        this.key = key;
        this.targetUrl = targetUrl;
        this.content = content;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV4Content getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_TARGET_URL)
    public String getTargetUrl() {
        return targetUrl;
    }

    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
