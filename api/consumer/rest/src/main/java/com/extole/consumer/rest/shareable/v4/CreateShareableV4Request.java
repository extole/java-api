package com.extole.consumer.rest.shareable.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@Deprecated // TODO remove ENG-10127
public final class CreateShareableV4Request {

    private static final String JSON_PROPERTY_ACCESS_TOKEN = "access_token";
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_TARGET_URL = "target_url";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_LABEL = "label";

    @Deprecated // TODO remove ENG-10127
    private final String accessToken;
    private final String key;
    private final String targetUrl;
    private final String code;
    private final ShareableV4Content content;
    private final String label;

    @JsonCreator
    public CreateShareableV4Request(
        @Nullable @JsonProperty(JSON_PROPERTY_ACCESS_TOKEN) String accessToken,
        @Nullable @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_CODE) String code,
        @JsonProperty(JSON_PROPERTY_TARGET_URL) String targetUrl,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV4Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label) {
        this.accessToken = accessToken;
        this.key = key;
        this.code = code;
        this.targetUrl = targetUrl;
        this.content = content;
        this.label = label;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV4Content getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_TARGET_URL)
    public String getTargetUrl() {
        return targetUrl;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
