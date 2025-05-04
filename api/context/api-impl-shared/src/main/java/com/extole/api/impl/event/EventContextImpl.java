package com.extole.api.impl.event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import com.extole.api.event.EventContext;
import com.extole.api.event.geoip.GeoIp;
import com.extole.api.impl.event.geoip.GeoIpImpl;
import com.extole.id.Id;

public class EventContextImpl implements EventContext {

    private final List<GeoIp> geoIps;
    private final Optional<String> appType;
    private final Optional<Id<?>> userId;
    private final Optional<com.extole.event.consumer.ApiType> apiType;

    public EventContextImpl(
        List<com.extole.common.ip.GeoIp> sourceGeoIps,
        Optional<String> appType,
        Optional<Id<?>> userId,
        Optional<com.extole.event.consumer.ApiType> apiType) {
        this.apiType = apiType;
        this.geoIps = ImmutableList.copyOf(sourceGeoIps.stream()
            .map(geoIp -> new GeoIpImpl(geoIp))
            .collect(Collectors.toList()));
        this.appType = appType;
        this.userId = userId;
    }

    @Override
    public GeoIp[] getSourceGeoIps() {
        return geoIps.toArray(new GeoIp[] {});
    }

    @Nullable
    @Override
    public String getAppType() {
        return appType.orElse(null);
    }

    @Nullable
    @Override
    public String getUserId() {
        return userId.map(Id::getValue).orElse(null);
    }

    @Override
    public boolean isAdminApi() {
        if (apiType.isPresent()) {
            return apiType.get() != com.extole.event.consumer.ApiType.CONSUMER;
        }

        return true;
    }

    public static EventContext empty() {
        return new EventContextImpl(List.of(), Optional.empty(), Optional.empty(), Optional.empty());
    }

}
