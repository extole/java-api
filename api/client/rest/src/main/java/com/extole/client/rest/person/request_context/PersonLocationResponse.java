package com.extole.client.rest.person.request_context;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.person.City;
import com.extole.client.rest.person.Country;
import com.extole.client.rest.person.Location;
import com.extole.client.rest.person.State;
import com.extole.common.lang.ToString;

public class PersonLocationResponse {

    private static final String IP_ADDRESS = "ip_address";
    private static final String DEVICE_ID = "device_id";
    private static final String COUNTRY = "country";
    private static final String STATE = "state";
    private static final String CITY = "city";
    private static final String LOCATION = "location";
    private static final String ZIP_CODE = "zip_code";
    private static final String ACCURACY_RADIUS_KM = "accuracy_radius_km";
    private static final String CREATED_DATE = "created_date";

    private final String ipAddress;
    private final String deviceId;
    private final Optional<Country> country;
    private final Optional<State> state;
    private final Optional<City> city;
    private final Optional<Location> location;
    private final Optional<String> zipCode;
    private final Optional<Integer> accuracyRadiusKm;
    private final ZonedDateTime createdDate;

    @JsonCreator
    public PersonLocationResponse(
        @JsonProperty(IP_ADDRESS) String ipAddress,
        @JsonProperty(DEVICE_ID) String deviceId,
        @JsonProperty(COUNTRY) Optional<Country> country,
        @JsonProperty(STATE) Optional<State> state,
        @JsonProperty(CITY) Optional<City> city,
        @JsonProperty(LOCATION) Optional<Location> location,
        @JsonProperty(ZIP_CODE) Optional<String> zipCode,
        @JsonProperty(ACCURACY_RADIUS_KM) Optional<Integer> accuracyRadiusKm,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate) {
        this.ipAddress = ipAddress;
        this.deviceId = deviceId;
        this.country = country;
        this.state = state;
        this.city = city;
        this.location = location;
        this.zipCode = zipCode;
        this.accuracyRadiusKm = accuracyRadiusKm;
        this.createdDate = createdDate;
    }

    @JsonProperty(IP_ADDRESS)
    public String getIpAddress() {
        return ipAddress;
    }

    @JsonProperty(DEVICE_ID)
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty(COUNTRY)
    public Optional<Country> getCountry() {
        return country;
    }

    @JsonProperty(STATE)
    public Optional<State> getState() {
        return state;
    }

    @JsonProperty(CITY)
    public Optional<City> getCity() {
        return city;
    }

    @JsonProperty(LOCATION)
    public Optional<Location> getLocation() {
        return location;
    }

    @JsonProperty(ZIP_CODE)
    public Optional<String> getZipCode() {
        return zipCode;
    }

    @JsonProperty(ACCURACY_RADIUS_KM)
    public Optional<Integer> getAccuracyRadiusKm() {
        return accuracyRadiusKm;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new PersonLocationResponse.Builder();
    }

    public static final class Builder {
        private String ipAdress;
        private String deviceId;
        private Optional<Country> country = Optional.empty();
        private Optional<State> state = Optional.empty();
        private Optional<City> city = Optional.empty();
        private Optional<Location> location = Optional.empty();
        private Optional<String> zipCode = Optional.empty();
        private Optional<Integer> accuracyRadiusKm = Optional.empty();
        private ZonedDateTime createdDate;

        private Builder() {
        }

        public Builder withIpAdress(String ipAdress) {
            this.ipAdress = ipAdress;
            return this;
        }

        public Builder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withCountry(Country country) {
            this.country = Optional.ofNullable(country);
            return this;
        }

        public Builder withState(State state) {
            this.state = Optional.ofNullable(state);
            return this;
        }

        public Builder withCity(City city) {
            this.city = Optional.ofNullable(city);
            return this;
        }

        public Builder withLocation(Location location) {
            this.location = Optional.ofNullable(location);
            return this;
        }

        public Builder withZipCode(String zipCode) {
            this.zipCode = Optional.ofNullable(zipCode);
            return this;
        }

        public Builder withAccuracyRadiusKm(Integer accuracyRadiusKm) {
            this.accuracyRadiusKm = Optional.ofNullable(accuracyRadiusKm);
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public PersonLocationResponse build() {
            return new PersonLocationResponse(ipAdress, deviceId, country, state, city, location, zipCode,
                accuracyRadiusKm, createdDate);
        }
    }
}
