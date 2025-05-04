package com.extole.client.rest.impl.person;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.extole.client.rest.person.City;
import com.extole.client.rest.person.request_context.PersonLocationResponse;
import com.extole.client.rest.person.v4.PersonRequestContextV4Response;
import com.extole.person.service.profile.request.context.Location;

@Component
public class RequestContextResponseMapper {

    public PersonRequestContextV4Response toRequestContextV4Response(Location location, ZoneId timeZone) {
        return new PersonRequestContextV4Response(location.getGeoIp().getIp().getValue(),
            location.getDeviceId().getId(), location.getCreatedAt().atZone(timeZone),
            mapCountry(location), mapState(location), mapCity(location), mapLocation(location),
            location.getGeoIp().getZipCode(), location.getGeoIp().getAccuracyRadiusKm());
    }

    public PersonLocationResponse toLocationResponse(Location location, ZoneId timeZone) {
        return PersonLocationResponse.builder()
            .withIpAdress(location.getGeoIp().getIp().getValue())
            .withDeviceId(location.getDeviceId().getId())
            .withCountry(mapCountry(location).orElse(null))
            .withState(mapState(location).orElse(null))
            .withCity(mapCity(location).orElse(null))
            .withLocation(mapLocation(location).orElse(null))
            .withZipCode(location.getGeoIp().getZipCode().orElse(null))
            .withAccuracyRadiusKm(location.getGeoIp().getAccuracyRadiusKm().orElse(null))
            .withCreatedDate(ZonedDateTime.ofInstant(location.getCreatedAt(), timeZone))
            .build();
    }

    private Optional<City> mapCity(Location location) {
        return location.getGeoIp().getCity().map(item -> new com.extole.client.rest.person.City(item.getName()));
    }

    private Optional<com.extole.client.rest.person.State> mapState(Location location) {
        return location.getGeoIp().getState()
            .map(item -> new com.extole.client.rest.person.State(item.getIsoCode(), item.getName()));
    }

    private Optional<com.extole.client.rest.person.Country> mapCountry(Location location) {
        return location.getGeoIp().getCountry()
            .map(item -> new com.extole.client.rest.person.Country(item.getIsoCode(), item.getName()));
    }

    private Optional<com.extole.client.rest.person.Location> mapLocation(Location location) {
        return location.getGeoIp().getLocation()
            .map(item -> new com.extole.client.rest.person.Location(item.getLatitude().orElse(null),
                item.getLongitude().orElse(null)));
    }
}
