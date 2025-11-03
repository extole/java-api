package com.extole.reporting.rest.audience.member;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class AudienceMemberWithDataResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_IDENTITY_KEY = "identity_key";
    private static final String JSON_IDENTITY_KEY_VALUE = "identity_key_value";
    private static final String JSON_EMAIL = "email";
    private static final String JSON_FIRST_NAME = "first_name";
    private static final String JSON_LAST_NAME = "last_name";
    private static final String JSON_PICTURE_URL = "picture_url";
    private static final String JSON_PARTNER_USER_ID = "partner_user_id";
    private static final String JSON_LOCALE = "locale";
    private static final String JSON_VERSION = "version";
    private static final String JSON_BLOCKED = "blocked";
    private static final String JSON_DATA = "data";

    private final String id;
    private final String identityKey;
    private final Optional<String> identityKeyValue;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String pictureUrl;
    private final String partnerUserId;
    private final PersonLocaleResponse locale;
    private final String version;
    private final boolean blocked;
    private final Map<String, PersonDataResponse> data;

    public AudienceMemberWithDataResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_IDENTITY_KEY) String identityKey,
        @JsonProperty(JSON_IDENTITY_KEY_VALUE) Optional<String> identityKeyValue,
        @JsonProperty(JSON_EMAIL) String email,
        @JsonProperty(JSON_FIRST_NAME) String firstName,
        @JsonProperty(JSON_LAST_NAME) String lastName,
        @JsonProperty(JSON_PICTURE_URL) String pictureUrl,
        @JsonProperty(JSON_PARTNER_USER_ID) String partnerUserId,
        @JsonProperty(JSON_LOCALE) PersonLocaleResponse locale,
        @JsonProperty(JSON_VERSION) String version,
        @JsonProperty(JSON_BLOCKED) boolean blocked,
        @JsonProperty(JSON_DATA) Map<String, PersonDataResponse> data) {
        this.id = id;
        this.identityKey = identityKey;
        this.identityKeyValue = identityKeyValue;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
        this.partnerUserId = partnerUserId;
        this.locale = locale;
        this.version = version;
        this.blocked = blocked;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_IDENTITY_KEY)
    public String getIdentityKey() {
        return identityKey;
    }

    @JsonProperty(JSON_IDENTITY_KEY_VALUE)
    public Optional<String> getIdentityKeyValue() {
        return identityKeyValue;
    }

    @JsonProperty(JSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty(JSON_LAST_NAME)
    public String getLastName() {
        return lastName;
    }

    @JsonProperty(JSON_PICTURE_URL)
    public String getPictureUrl() {
        return pictureUrl;
    }

    @JsonProperty(JSON_PARTNER_USER_ID)
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @JsonProperty(JSON_LOCALE)
    public PersonLocaleResponse getLocale() {
        return locale;
    }

    @JsonProperty(JSON_VERSION)
    public String getVersion() {
        return version;
    }

    @JsonProperty(JSON_BLOCKED)
    public boolean isBlocked() {
        return blocked;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, PersonDataResponse> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
