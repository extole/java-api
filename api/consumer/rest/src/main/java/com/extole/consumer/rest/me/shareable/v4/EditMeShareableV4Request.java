package com.extole.consumer.rest.me.shareable.v4;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.shareable.v4.ShareableV4Content;

@Deprecated // TODO remove ENG-10127
public final class EditMeShareableV4Request {

    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_TARGET_URL = "target_url";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";
    private static final String JSON_PROPERTY_LABEL = "label";

    private final String key;
    @Deprecated // TBD - OPEN TICKET
    private final String targetUrl;
    private final ShareableV4Content content;
    private final Map<String, String> data;
    private final String label;

    public EditMeShareableV4Request(
        @Nullable @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_TARGET_URL) String targetUrl,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV4Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label) {
        this.key = key;
        this.targetUrl = targetUrl;
        this.content = content;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
        this.label = label;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV4Content getContent() {
        return content;
    }

    @Deprecated // TBD - OPEN TICKET Use a <code>redirect</code> data attribute instead
    @Nullable
    @JsonProperty(JSON_PROPERTY_TARGET_URL)
    public String getTargetUrl() {
        return targetUrl;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
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
