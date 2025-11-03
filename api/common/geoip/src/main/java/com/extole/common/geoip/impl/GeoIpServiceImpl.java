package com.extole.common.geoip.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AbstractCityResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Subdivision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.common.geoip.GeoIpService;
import com.extole.common.ip.City;
import com.extole.common.ip.Country;
import com.extole.common.ip.GeoIp;
import com.extole.common.ip.Location;
import com.extole.common.ip.State;

@Component
public class GeoIpServiceImpl implements GeoIpService {
    private static final Logger LOG = LoggerFactory.getLogger(GeoIpServiceImpl.class);
    private final DatabaseReader databaseReader;

    @Autowired
    public GeoIpServiceImpl(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    @Override
    public GeoIp fromInetAddress(InetAddress inetAddress) {
        try {
            Optional<CountryResponse> country = databaseReader.tryCountry(inetAddress);
            Optional<CityResponse> city = databaseReader.tryCity(inetAddress);
            Optional<Subdivision> subdivision = city.map(AbstractCityResponse::getMostSpecificSubdivision);

            return GeoIp.builder()
                .withInetAddress(inetAddress)
                .withCountry(country.map(item -> toCountry(item)).orElse(null))
                .withCity(city.map(item -> toCity(item)).orElse(null))
                .withLocation(city.map(item -> toLocation(item)).orElse(null))
                .withState(subdivision.map(item -> toState(item)).orElse(null))
                .withZipCode(city.map(item -> item.getPostal().getCode()).orElse(null))
                .withAccuracyRadiusKm(city.map(item -> item.getLocation().getAccuracyRadius()).orElse(null))
                .build();
        } catch (IOException | GeoIp2Exception e) {
            LOG.error("Unable to evaluate GeoLocation for IP={}", inetAddress, e);
        }
        return GeoIp.newInstance(inetAddress);
    }

    private Country toCountry(CountryResponse country) {
        return new Country(country.getCountry().getIsoCode(), country.getCountry().getName());
    }

    private State toState(Subdivision subdivision) {
        return new State(subdivision.getIsoCode(), subdivision.getName());
    }

    private City toCity(CityResponse city) {
        return new City(city.getCity().getName());
    }

    private Location toLocation(CityResponse city) {
        if (city.getLocation() == null) {
            return new Location(null, null);
        }
        return new Location(city.getLocation().getLatitude(), city.getLocation().getLongitude());
    }
}
