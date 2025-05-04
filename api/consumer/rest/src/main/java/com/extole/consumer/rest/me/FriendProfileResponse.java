package com.extole.consumer.rest.me;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FriendProfileResponse {
    private static final String JSON_PROPERTY_ID = "id";
    private static final String JSON_PROPERTY_FIRST_NAME = "first_name";
    private static final String JSON_PROPERTY_PROFILE_PICTURE_URL = "image_url";
    private static final String JSON_PROPERTY_EVENTS = "events";

    private final String personId;
    private final String firstName;
    private final String profilePictureUrl;
    private final List<FriendEvent> events;

    @JsonCreator
    public FriendProfileResponse(
        @JsonProperty(JSON_PROPERTY_ID) String personId,
        @JsonProperty(JSON_PROPERTY_FIRST_NAME) String firstName,
        @JsonProperty(JSON_PROPERTY_PROFILE_PICTURE_URL) String profilePictureUrl,
        @JsonProperty(JSON_PROPERTY_EVENTS) List<FriendEvent> events) {
        this.personId = personId;
        this.firstName = firstName;
        this.profilePictureUrl = profilePictureUrl;
        this.events = events;
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

    @Deprecated // TODO remove ENG-10126
    @JsonProperty(JSON_PROPERTY_EVENTS)
    public List<FriendEvent> getEvents() {
        return events;
    }

    @Deprecated // TODO remove ENG-10126
    public static class FriendEvent {
        private final String campaignId;
        private final String stepName;
        private final String eventDate;
        private final String email;

        public FriendEvent(
            @JsonProperty("campaign_id") String campaignId,
            @JsonProperty("step_name") String stepName,
            @JsonProperty("event_date") String eventDate,
            @JsonProperty("email") String email) {
            this.campaignId = campaignId;
            this.stepName = stepName;
            this.eventDate = eventDate;
            this.email = email;
        }

        @JsonProperty("campaign_id")
        public String getCampaignId() {
            return campaignId;
        }

        @JsonProperty("event_date")
        public String getEventDate() {
            return eventDate;
        }

        @JsonProperty("step_name")
        public String getStepName() {
            return stepName;
        }

        @Deprecated // TODO remove ENG-10126
        @JsonProperty("email")
        public String getEmail() {
            return email;
        }
    }
}
