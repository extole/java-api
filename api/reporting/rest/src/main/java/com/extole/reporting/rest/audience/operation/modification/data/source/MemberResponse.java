package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class MemberResponse {

    private static final String EMAIL = "email";
    private static final String IDENTITY_KEY = "identity_key";
    private static final String IDENTITY_KEY_VALUE = "identity_key_value";
    private static final String DATA = "data";

    private final String identityKey;
    private final String identityKeyValue;
    private final Map<String, String> data;

    public MemberResponse(
        @JsonProperty(IDENTITY_KEY) String identityKey,
        @JsonProperty(IDENTITY_KEY_VALUE) String identityKeyValue,
        @JsonProperty(DATA) Map<String, String> data) {
        this.identityKey = identityKey;
        this.identityKeyValue = identityKeyValue;
        this.data = ImmutableMap.copyOf(data);
    }

    @Deprecated // TODO remove ENG-24531
    @JsonProperty(EMAIL)
    public String getEmail() {
        return identityKeyValue;
    }

    @JsonProperty(IDENTITY_KEY)
    public String getIdentityKey() {
        return identityKey;
    }

    @JsonProperty(IDENTITY_KEY_VALUE)
    public String getIdentityKeyValue() {
        return identityKeyValue;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
