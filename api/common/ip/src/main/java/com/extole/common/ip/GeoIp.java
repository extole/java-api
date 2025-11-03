package com.extole.common.ip;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.InetAddresses;

import com.extole.common.lang.ToString;

public final class GeoIp {

    private static final String JSON_IP_ADDRESS = "ip_address";
    private static final String JSON_COUNTRY = "country";
    private static final String JSON_STATE = "state";
    private static final String JSON_CITY = "city";
    private static final String JSON_LOCATION = "location";
    private static final String JSON_ZIP_CODE = "zip_code";
    private static final String JSON_ACCURACY_RADIUS_KM = "accuracy_radius_km";

    private final Ip ipAddress;
    private final Country country;
    private final State state;
    private final City city;
    private final Location location;
    private final String zipCode;
    private final Integer accuracyRadiusKm;

    @JsonCreator
    private GeoIp(@JsonProperty(JSON_IP_ADDRESS) Ip ipAddress,
        @JsonProperty(JSON_COUNTRY) Country country,
        @JsonProperty(JSON_STATE) State state,
        @JsonProperty(JSON_CITY) City city,
        @JsonProperty(JSON_LOCATION) Location location,
        @JsonProperty(JSON_ZIP_CODE) String zipCode,
        @JsonProperty(JSON_ACCURACY_RADIUS_KM) Integer accuracyRadiusKm) {
        this.ipAddress = ipAddress;
        this.country = country;
        this.state = state;
        this.city = city;
        this.location = location;
        this.zipCode = zipCode;
        this.accuracyRadiusKm = accuracyRadiusKm;
    }

    @JsonProperty(JSON_IP_ADDRESS)
    public Ip getIp() {
        return ipAddress;
    }

    @JsonProperty(JSON_COUNTRY)
    public Optional<Country> getCountry() {
        return Optional.ofNullable(country);
    }

    @JsonProperty(JSON_STATE)
    public Optional<State> getState() {
        return Optional.ofNullable(state);
    }

    @JsonProperty(JSON_CITY)
    public Optional<City> getCity() {
        return Optional.ofNullable(city);
    }

    @JsonProperty(JSON_LOCATION)
    public Optional<Location> getLocation() {
        return Optional.ofNullable(location);
    }

    @JsonProperty(JSON_ZIP_CODE)
    public Optional<String> getZipCode() {
        return Optional.ofNullable(zipCode);
    }

    @JsonProperty(JSON_ACCURACY_RADIUS_KM)
    public Optional<Integer> getAccuracyRadiusKm() {
        return Optional.ofNullable(accuracyRadiusKm);
    }

    @JsonIgnore
    public boolean isLocal() {
        // from JavaDoc: This deliberately avoids all nameservice lookups (e.g. no DNS).
        InetAddress inetAddress = InetAddresses.forString(ipAddress.getValue());
        return inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || otherObject.getClass() != GeoIp.class) {
            return false;
        }

        GeoIp otherIp = (GeoIp) otherObject;
        return Objects.equals(ipAddress, otherIp.ipAddress);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static GeoIp newInstance(InetAddress inetAddress) {
        return new GeoIp(Ip.valueOf(inetAddress.getHostAddress()), null, null, null, null, null, null);
    }

    public static GeoIpBuilder builder() {
        return new GeoIpBuilder();
    }

    public static final class GeoIpBuilder {
        private InetAddress inetAddress;
        private Country country;
        private State state;
        private City city;
        private Location location;
        private String zipCode;
        private Integer accuracyRadiusKm;

        private GeoIpBuilder() {
        }

        public GeoIpBuilder withInetAddress(InetAddress val) {
            inetAddress = val;
            return this;
        }

        public GeoIpBuilder withCountry(Country val) {
            country = val;
            return this;
        }

        public GeoIpBuilder withState(State val) {
            state = val;
            return this;
        }

        public GeoIpBuilder withCity(City val) {
            city = val;
            return this;
        }

        public GeoIpBuilder withLocation(Location val) {
            location = val;
            return this;
        }

        public GeoIpBuilder withZipCode(String val) {
            zipCode = val;
            return this;
        }

        public GeoIpBuilder withAccuracyRadiusKm(Integer val) {
            accuracyRadiusKm = val;
            return this;
        }

        public GeoIp build() {
            return new GeoIp(Ip.valueOf(inetAddress.getHostAddress()), country, state, city, location, zipCode,
                accuracyRadiusKm);
        }

    }

}
