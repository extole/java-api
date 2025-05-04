package com.extole.consumer.rest.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonProfileUpdateRequest {
    private static final String JSON_PROPERTY_ACCESS_TOKEN = "access_token";
    private static final String JSON_PROPERTY_EMAIL = "email";
    private static final String JSON_PROPERTY_FIRST_NAME = "first_name";
    private static final String JSON_PROPERTY_LAST_NAME = "last_name";
    private static final String JSON_PROPERTY_PROFILE_PICTURE_URL = "profile_picture_url";
    private static final String JSON_PROPERTY_PARTNER_USER_ID = "partner_user_id";

    private final String accessToken;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String profilePictureUrl;
    private final String partnerUserId;

    @JsonCreator
    public PersonProfileUpdateRequest(
        @JsonProperty(JSON_PROPERTY_ACCESS_TOKEN) String accessToken,
        @JsonProperty(JSON_PROPERTY_EMAIL) String email,
        @JsonProperty(JSON_PROPERTY_FIRST_NAME) String firstName,
        @JsonProperty(JSON_PROPERTY_LAST_NAME) String lastName,
        @JsonProperty(JSON_PROPERTY_PROFILE_PICTURE_URL) String profilePictureUrl,
        @JsonProperty(JSON_PROPERTY_PARTNER_USER_ID) String partnerUserId) {
        this.accessToken = accessToken;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePictureUrl = profilePictureUrl;
        this.partnerUserId = partnerUserId;
    }

    @Deprecated // TODO - Remove - unused in endpoints ENG-14282
    @JsonProperty(JSON_PROPERTY_ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty(JSON_PROPERTY_EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(JSON_PROPERTY_FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty(JSON_PROPERTY_LAST_NAME)
    public String getLastName() {
        return lastName;
    }

    @JsonProperty(JSON_PROPERTY_PROFILE_PICTURE_URL)
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @JsonProperty(JSON_PROPERTY_PARTNER_USER_ID)
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String accessToken;
        private String email;
        private String firstName;
        private String lastName;
        private String profilePictureUrl;
        private String partnerUserId;

        private Builder() {
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withProfilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public Builder withPartnerUserId(String partnerUserId) {
            this.partnerUserId = partnerUserId;
            return this;
        }

        public PersonProfileUpdateRequest build() {
            return new PersonProfileUpdateRequest(accessToken, email, firstName, lastName, profilePictureUrl,
                partnerUserId);
        }
    }
}
