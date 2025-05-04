package com.extole.client.rest.person;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.identity.IdentityKey;
import com.extole.common.lang.ToString;

@Schema(description = "Extole PersonResponse")
public class PersonResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_IDENTITY_ID = "identity_id";
    private static final String JSON_IDENTITY_KEY = "identity_key";
    private static final String JSON_IDENTITY_KEY_VALUE = "identity_key_value";
    private static final String JSON_LOCALE = "locale";
    private static final String JSON_VERSION = "version";

    private final String id;
    private final Optional<String> identityId;
    private final IdentityKey identityKey;
    private final Optional<String> identityKeyValue;
    private final PersonLocaleResponse locale;
    private final String version;

    public PersonResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_IDENTITY_ID) Optional<String> identityId,
        @JsonProperty(JSON_IDENTITY_KEY) IdentityKey identityKey,
        @JsonProperty(JSON_IDENTITY_KEY_VALUE) Optional<String> identityKeyValue,
        @JsonProperty(JSON_LOCALE) PersonLocaleResponse locale,
        @JsonProperty(JSON_VERSION) String version) {
        this.id = id;
        this.identityId = identityId;
        this.identityKey = identityKey;
        this.identityKeyValue = identityKeyValue;
        this.locale = locale;
        this.version = version;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_IDENTITY_ID)
    public Optional<String> getIdentityId() {
        return identityId;
    }

    @JsonProperty(JSON_IDENTITY_KEY)
    public IdentityKey getIdentityKey() {
        return identityKey;
    }

    @JsonProperty(JSON_IDENTITY_KEY_VALUE)
    public Optional<String> getIdentityKeyValue() {
        return identityKeyValue;
    }

    @JsonProperty(JSON_LOCALE)
    public PersonLocaleResponse getLocale() {
        return locale;
    }

    @JsonProperty(JSON_VERSION)
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
