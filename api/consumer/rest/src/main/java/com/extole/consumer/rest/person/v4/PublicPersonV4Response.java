package com.extole.consumer.rest.person.v4;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove ENG-10143
public class PublicPersonV4Response {
    private static final String JSON_PROPERTY_ID = "id";
    private static final String JSON_PROPERTY_FIRST_NAME = "first_name";
    private static final String JSON_PROPERTY_PROFILE_PICTURE_URL = "image_url";
    private static final String JSON_PROPERTY_PUBLIC_PARAMETERS = "parameters";

    private final String personId;
    private final String firstName;
    private final String profilePictureUrl;
    private final Map<String, Object> publicParameters;

    @JsonCreator
    public PublicPersonV4Response(
        @JsonProperty(JSON_PROPERTY_ID) String personId,
        @JsonProperty(JSON_PROPERTY_FIRST_NAME) String firstName,
        @JsonProperty(JSON_PROPERTY_PROFILE_PICTURE_URL) String profilePictureUrl,
        @JsonProperty(JSON_PROPERTY_PUBLIC_PARAMETERS) Map<String, Object> publicParameters) {
        this.personId = personId;
        this.firstName = firstName;
        this.profilePictureUrl = profilePictureUrl;
        this.publicParameters = publicParameters;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public String getPersonId() {
        return personId;
    }

    @JsonProperty(JSON_PROPERTY_FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty(JSON_PROPERTY_PROFILE_PICTURE_URL)
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @JsonProperty(JSON_PROPERTY_PUBLIC_PARAMETERS)
    public Map<String, Object> getParameters() {
        return publicParameters;
    }
}
