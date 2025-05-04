package com.extole.client.rest.person;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class Location {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private final Double latitude;
    private final Double longitude;

    @JsonCreator
    public Location(@JsonProperty(LATITUDE) Double latitude,
        @JsonProperty(LONGITUDE) Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @JsonProperty(LATITUDE)
    public Optional<Double> getLatitude() {
        return Optional.ofNullable(latitude);
    }

    @JsonProperty(LONGITUDE)
    public Optional<Double> getLongitude() {
        return Optional.ofNullable(longitude);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
