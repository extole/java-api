package com.extole.consumer.rest.me;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class MyProfileResponse {
    private static final String JSON_PROPERTY_ID = "id";
    private static final String JSON_PROPERTY_EMAIL = "email";
    private static final String JSON_PROPERTY_FIRST_NAME = "first_name";
    private static final String JSON_PROPERTY_LAST_NAME = "last_name";
    private static final String JSON_PROPERTY_PROFILE_PICTURE_URL = "profile_picture_url";
    private static final String JSON_PROPERTY_PARTNER_USER_ID = "partner_user_id";
    private static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private static final String COOKIE_CONSENT = "cookie_consent";
    private static final String COOKIE_CONSENT_TYPE = "cookie_consent_type";
    private static final String PROCESSING_CONSENT = "processing_consent";
    private static final String PROCESSING_CONSENT_TYPE = "processing_consent_type";
    private static final String JSON_LOCALE = "locale";
    // TODO JSON field name mismatch with PublicPersonProfileResponse.profile_picture_url

    private final String personId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String profilePictureUrl;
    private final String partnerUserId;
    private final String cookieConsent;
    private final ConsentType cookieConsentType;
    private final String processingConsent;
    private final ConsentType processingConsentType;
    private final Map<String, Object> parameters;
    private final String locale;

    @JsonCreator
    public MyProfileResponse(
        @JsonProperty(JSON_PROPERTY_ID) String personId,
        @JsonProperty(JSON_PROPERTY_EMAIL) String email,
        @JsonProperty(JSON_PROPERTY_FIRST_NAME) String firstName,
        @JsonProperty(JSON_PROPERTY_LAST_NAME) String lastName,
        @JsonProperty(JSON_PROPERTY_PROFILE_PICTURE_URL) String profilePictureUrl,
        @JsonProperty(JSON_PROPERTY_PARTNER_USER_ID) String partnerUserId,
        @JsonProperty(COOKIE_CONSENT) String cookieConsent,
        @JsonProperty(COOKIE_CONSENT_TYPE) ConsentType cookieConsentType,
        @JsonProperty(PROCESSING_CONSENT) String processingConsent,
        @JsonProperty(PROCESSING_CONSENT_TYPE) ConsentType processingConsentType,
        @JsonProperty(JSON_PROPERTY_PARAMETERS) Map<String, Object> parameters,
        @Nullable @JsonProperty(JSON_LOCALE) String locale) {
        this.personId = personId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePictureUrl = profilePictureUrl;
        this.partnerUserId = partnerUserId;
        this.cookieConsent = cookieConsent;
        this.cookieConsentType = cookieConsentType;
        this.processingConsent = processingConsent;
        this.processingConsentType = processingConsentType;
        this.parameters = parameters;
        this.locale = locale;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public String getPersonId() {
        return personId;
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

    @JsonProperty(COOKIE_CONSENT)
    public String getCookieConsent() {
        return cookieConsent;
    }

    @JsonProperty(COOKIE_CONSENT_TYPE)
    public ConsentType getCookieConsentType() {
        return cookieConsentType;
    }

    @JsonProperty(PROCESSING_CONSENT)
    public String getProcessingConsent() {
        return processingConsent;
    }

    @JsonProperty(PROCESSING_CONSENT_TYPE)
    public ConsentType getProcessingConsentType() {
        return processingConsentType;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Nullable
    @JsonProperty(JSON_LOCALE)
    public String getLocale() {
        return locale;
    }

    public enum ConsentType {
        EXTOLE, CLIENT, UNSET
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
