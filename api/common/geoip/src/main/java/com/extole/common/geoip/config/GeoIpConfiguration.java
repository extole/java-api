package com.extole.common.geoip.config;

import java.io.IOException;
import java.io.InputStream;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GeoIpConfiguration {

    private final String geoIpDbFileName;

    @Autowired
    public GeoIpConfiguration(@Value("${geo.ip.db.file.name:GeoIP2-City.mmdb}") String geoIpDbFileName) {
        this.geoIpDbFileName = geoIpDbFileName;
    }

    @Bean // TODO encapsulate this ENG-12711-2
    public DatabaseReader databaseReader() throws IOException {
        try (InputStream geoIpDatabase = new ClassPathResource(geoIpDbFileName).getInputStream()) {
            return new DatabaseReader.Builder(geoIpDatabase).withCache(new CHMCache()).build();
        }
    }
}
