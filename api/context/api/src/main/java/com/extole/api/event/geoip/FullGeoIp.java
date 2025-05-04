package com.extole.api.event.geoip;

import javax.annotation.Nullable;

public interface FullGeoIp {
    String getIpAddress();

    @Nullable
    Country getCountry();

    @Nullable
    State getState();

    @Nullable
    String getCity();

    @Nullable
    Location getLocation();

    @Nullable
    String getZipCode();

    @Nullable
    Integer getAccuracyRadiusKm();

    @Nullable
    Integer getAutonomousSystemNumber();

    @Nullable
    String getAutonomousSystemOrganization();

    @Nullable
    String getConnectionType();

    @Nullable
    Boolean isAnonymous();

    @Nullable
    Boolean isAnonymousVpn();

    @Nullable
    Boolean isHostingProvider();

    @Nullable
    Boolean isPublicProxy();

    @Nullable
    Boolean isResidentialProxy();

    @Nullable
    Boolean isTorExitNode();
}
