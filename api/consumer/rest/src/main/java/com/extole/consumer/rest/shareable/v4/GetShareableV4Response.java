package com.extole.consumer.rest.shareable.v4;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.me.shareable.v4.MeShareableV4Response;

@Deprecated // TODO remove ENG-10127
public class GetShareableV4Response extends MeShareableV4Response {

    private static final String JSON_PROPERTY_PERSON_ID = "person_id";

    private final String personId;

    public GetShareableV4Response(
        @JsonProperty(JSON_PROPERTY_SHAREABLE_ID) String id,
        @JsonProperty(JSON_PROPERTY_CODE) String code,
        @JsonProperty(JSON_PROPERTY_LINK) String link,
        @JsonProperty(JSON_PROPERTY_KEY) String key,
        @JsonProperty(JSON_PROPERTY_CONTENT) ShareableV4Content content,
        @Nullable @JsonProperty(JSON_PROPERTY_DATA) Map<String, String> data,
        @Nullable @JsonProperty(JSON_PROPERTY_LABEL) String label,
        @JsonProperty(JSON_PROPERTY_PERSON_ID) String personId) {
        super(id, code, link, key, content, data, label);
        this.personId = personId;
    }

    @JsonProperty(JSON_PROPERTY_PERSON_ID)
    public String getPersonId() {
        return personId;
    }

}
