package com.extole.client.rest.shareable.v2;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public final class ShareableV2Response {

    private static final String JSON_PROPERTY_ID = "shareable_id";
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_LINK = "link";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";
    private static final String JSON_PROPERTY_LABEL = "label";

    private final String id;
    private final String key;
    private final String code;
    private final String link;
    private final ShareableV2Content content;
    private final Map<String, String> data;
    private final String label;

    @JsonCreator
    public ShareableV2Response(
        @JsonProperty(JSON_PROPERTY_ID) String id,
        @JsonProperty(JSON_PROPERTY_KEY) String key,
        @JsonProperty(JSON_PROPERTY_CODE) String code,
        @JsonProperty(JSON_PROPERTY_LINK) String link,
        @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV2Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label) {
        this.id = id;
        this.key = key;
        this.code = code;
        this.link = link;
        this.content = content;
        this.data = data;
        this.label = label;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV2Content getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_LINK)
    public String getLink() {
        return link;
    }

    @JsonProperty(JSON_PROPERTY_KEY)
    public String getKey() {
        return key;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

}
