package com.extole.consumer.rest.me.shareable.v4;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.shareable.v4.ShareableV4Content;

@Deprecated // TODO remove ENG-10127
public class MeShareableV4Response {

    protected static final String JSON_PROPERTY_SHAREABLE_ID = "shareable_id";
    protected static final String JSON_PROPERTY_CODE = "code";
    protected static final String JSON_PROPERTY_LINK = "link";
    protected static final String JSON_PROPERTY_KEY = "key";
    protected static final String JSON_PROPERTY_CONTENT = "content";
    protected static final String JSON_PROPERTY_DATA = "data";
    protected static final String JSON_PROPERTY_LABEL = "label";

    private final String id;
    private final String code;
    private final String link;
    private final String key;
    private final ShareableV4Content content;
    private final Map<String, String> data;
    private final String label;

    public MeShareableV4Response(
        @JsonProperty(JSON_PROPERTY_SHAREABLE_ID) String id,
        @JsonProperty(JSON_PROPERTY_CODE) String code,
        @JsonProperty(JSON_PROPERTY_LINK) String link,
        @JsonProperty(JSON_PROPERTY_KEY) String key,
        @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV4Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label) {
        this.id = id;
        this.code = code;
        this.link = link;
        this.key = key;
        this.content = content;
        this.data = data;
        this.label = label;
    }

    @JsonProperty(JSON_PROPERTY_SHAREABLE_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT)
    public ShareableV4Content getContent() {
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
