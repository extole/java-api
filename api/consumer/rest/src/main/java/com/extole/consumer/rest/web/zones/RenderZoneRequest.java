package com.extole.consumer.rest.web.zones;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RenderZoneRequest {

    private static final String EVENT_NAME = "event_name";
    private static final String JWT = "jwt";
    private static final String ID_TOKEN = "id_token";
    private static final String DATA = "data";

    private final String eventName;
    private final String jwt;
    private final String idToken;
    private final Map<String, Object> data;

    @JsonCreator
    public RenderZoneRequest(@Nullable @JsonProperty(EVENT_NAME) String eventName,
        @Nullable @JsonProperty(JWT) String jwt,
        @Nullable @JsonProperty(ID_TOKEN) String idToken,
        @Nullable @JsonProperty(value = DATA) Map<String, Object> data) {
        this.eventName = eventName;
        this.jwt = jwt;
        this.idToken = idToken;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(data);
    }

    @JsonProperty(EVENT_NAME)
    public Optional<String> getEventName() {
        return Optional.ofNullable(eventName);
    }

    @JsonProperty(JWT)
    public Optional<String> getJwt() {
        return Optional.ofNullable(jwt);
    }

    @JsonProperty(ID_TOKEN)
    public Optional<String> getIdToken() {
        return Optional.ofNullable(idToken);
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
        private String jwt;
        private String idToken;
        private Map<String, Object> data;

        private Builder() {
        }

        public Builder withZoneName(String zoneName) {
            this.zoneName = zoneName;
            return this;
        }

        public Builder withJwt(String jwt) {
            this.jwt = jwt;
            return this;
        }

        public Builder withIdToken(String idToken) {
            this.idToken = idToken;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public RenderZoneRequest build() {
            return new RenderZoneRequest(zoneName, jwt, idToken, data);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
