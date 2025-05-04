package com.extole.client.zone.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientRenderZoneRequest {

    public static final String DATA_EMAIL = "email";
    public static final String DATA_PARTNER_USER_ID = "partner_user_id";
    public static final String DATA_PERSON_ID = "person_id";
    public static final String DATA_EVENT_TIME = "event_time";

    private static final String ZONE_NAME = "zone_name";
    private static final String DATA = "data";

    private final String zoneName;
    private final Map<String, Object> data;

    @JsonCreator
    public ClientRenderZoneRequest(
        @JsonProperty(ZONE_NAME) String zoneName,
        @JsonProperty(value = DATA) Map<String, Object> data) {
        this.zoneName = zoneName;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    @JsonProperty(ZONE_NAME)
    public String getZoneName() {
        return zoneName;
    }

    @JsonProperty(DATA)
    public Map<String, Object> getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String zoneName;
        private Map<String, Object> data;

        private Builder() {
        }

        public Builder withZoneName(String zoneName) {
            this.zoneName = zoneName;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            this.data = Collections.unmodifiableMap(new HashMap<>(data));
            return this;
        }

        public ClientRenderZoneRequest build() {
            return new ClientRenderZoneRequest(zoneName, data);
        }

    }

}
