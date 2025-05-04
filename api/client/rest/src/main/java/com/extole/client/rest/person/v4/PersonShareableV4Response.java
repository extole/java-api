package com.extole.client.rest.person.v4;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public final class PersonShareableV4Response {

    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_KEY = "key";
    private static final String JSON_PROPERTY_LABEL = "label";
    private static final String JSON_PROPERTY_LINK = "link";
    private static final String JSON_PROPERTY_PERSON_ID = "person_id";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_DATA = "data";

    private final String code;
    private final String key;
    private final String label;
    private final String link;
    private final String personId;
    private final PersonShareableContentV4Response content;
    private final Map<String, String> data;

    @JsonCreator
    public PersonShareableV4Response(
        @JsonProperty(JSON_PROPERTY_CODE) String code,
        @JsonProperty(JSON_PROPERTY_KEY) String key,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label,
        @JsonProperty(JSON_PROPERTY_LINK) String link,
        @JsonProperty(JSON_PROPERTY_PERSON_ID) String personId,
        @Nullable @JsonProperty(JSON_PROPERTY_CONTENT) PersonShareableContentV4Response content,
        @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data) {
        this.code = code;
        this.key = key;
        this.label = label;
        this.link = link;
        this.personId = personId;
        this.content = content;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
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

    @JsonProperty(JSON_PROPERTY_PERSON_ID)
    public String getPersonId() {
        return personId;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public PersonShareableContentV4Response getContent() {
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

}
