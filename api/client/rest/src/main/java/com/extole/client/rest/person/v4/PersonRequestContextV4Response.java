package com.extole.client.rest.person.v4;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.person.City;
import com.extole.client.rest.person.Country;
import com.extole.client.rest.person.Location;
import com.extole.client.rest.person.State;
import com.extole.common.lang.ToString;

public class PersonRequestContextV4Response {

    private static final String JSON_IP = "ip";
    private static final String JSON_DEVICE_ID = "device_id";
    private static final String JSON_CREATED_AT = "created_at";
    private static final String JSON_COUNTRY = "country";
    private static final String JSON_STATE = "state";
    private static final String JSON_CITY = "city";
    private static final String JSON_LOCATION = "location";
    private static final String JSON_ZIP_CODE = "zip_code";
    private static final String JSON_ACCURACY_RADIUS_KM = "accuracy_radius_km";

    private final String ip;
    private final String deviceId;
    private final ZonedDateTime createdAt;
    private final Optional<Country> country;
    private final Optional<State> state;
    private final Optional<City> city;
    private final Optional<Location> location;
    private final Optional<String> zipCode;
    private final Optional<Integer> accuracyRadiusKm;

    @JsonCreator
    public PersonRequestContextV4Response(@JsonProperty(JSON_IP) String ip,
        @JsonProperty(JSON_DEVICE_ID) String deviceId,
        @JsonProperty(JSON_CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(JSON_COUNTRY) Optional<Country> country,
        @JsonProperty(JSON_STATE) Optional<State> state,
        @JsonProperty(JSON_CITY) Optional<City> city,
        @JsonProperty(JSON_LOCATION) Optional<Location> location,
        @JsonProperty(JSON_ZIP_CODE) Optional<String> zipCode,
        @JsonProperty(JSON_ACCURACY_RADIUS_KM) Optional<Integer> accuracyRadiusKm) {
        this.ip = ip;
        this.deviceId = deviceId;
        this.createdAt = createdAt;
        this.country = country;
        this.state = state;
        this.city = city;
        this.location = location;
        this.zipCode = zipCode;
        this.accuracyRadiusKm = accuracyRadiusKm;
    }

    @JsonProperty(JSON_IP)
    public String getIp() {
        return ip;
    }

    @JsonProperty(JSON_DEVICE_ID)
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty(JSON_CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(JSON_COUNTRY)
    public Optional<Country> getCountry() {
        return country;
    }

    @JsonProperty(JSON_STATE)
    public Optional<State> getState() {
        return state;
    }

    @JsonProperty(JSON_CITY)
    public Optional<City> getCity() {
        return city;
    }

    @JsonProperty(JSON_LOCATION)
    public Optional<Location> getLocation() {
        return location;
    }

    @JsonProperty(JSON_ZIP_CODE)
    public Optional<String> getZipCode() {
        return zipCode;
    }

    @JsonProperty(JSON_ACCURACY_RADIUS_KM)
    public Optional<Integer> getAccuracyRadiusKm() {
        return accuracyRadiusKm;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
