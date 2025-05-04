package com.extole.client.rest.person.v4;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.person.PersonLocaleResponse;
import com.extole.common.lang.ToString;

@Schema(description = "Extole PersonResponse")
public class PersonV4Response {

    private static final String JSON_ID = "id";
    private static final String JSON_EMAIL = "email";
    private static final String JSON_FIRST_NAME = "first_name";
    private static final String JSON_LAST_NAME = "last_name";
    private static final String JSON_PICTURE_URL = "picture_url";
    private static final String JSON_PARTNER_USER_ID = "partner_user_id";
    private static final String JSON_LOCALE = "locale";
    private static final String JSON_VERSION = "version";
    private static final String JSON_BLOCKED = "blocked";

    private final String id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String pictureUrl;
    private final String partnerUserId;
    private final PersonLocaleResponse locale;
    private final String version;
    private final boolean blocked;

    public PersonV4Response(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_EMAIL) String email,
        @JsonProperty(JSON_FIRST_NAME) String firstName,
        @JsonProperty(JSON_LAST_NAME) String lastName,
        @JsonProperty(JSON_PICTURE_URL) String pictureUrl,
        @JsonProperty(JSON_PARTNER_USER_ID) String partnerUserId,
        @JsonProperty(JSON_LOCALE) PersonLocaleResponse locale,
        @JsonProperty(JSON_VERSION) String version,
        @JsonProperty(JSON_BLOCKED) boolean blocked) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
        this.partnerUserId = partnerUserId;
        this.locale = locale;
        this.version = version;
        this.blocked = blocked;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @Schema(description = "The first name of the friend.")
    @JsonProperty(JSON_FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @Schema(description = "The last name of the friend.")
    @JsonProperty(JSON_LAST_NAME)
    public String getLastName() {
        return lastName;
    }

    @Schema(description = "A URL that has a picture of the person.")
    @JsonProperty(JSON_PICTURE_URL)
    public String getPictureUrl() {
        return pictureUrl;
    }

    @Schema(description = "Your unique identifier for this person.")
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

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
