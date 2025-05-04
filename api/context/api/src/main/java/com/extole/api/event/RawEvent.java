package com.extole.api.event;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.ClientDomainContext;

@Schema
public interface RawEvent {
    enum ApiType {
        CONSUMER, CLIENT, EVENT_DISPATCHER, CLIENT_ZONE, REWARDS, WEBHOOK_DISPATCHER
    }

    String getApiType();

    ClientDomainContext getClientDomainContext();

    String getRawEventId();

    String getRequestTime();

    String getEventName();

    String getUrl();

    @Deprecated // TODO Use getUrl instead ENG-22174
    String getIncomingUrl();

    String getReferrer();

    @Nullable
    String getHttpRequestBody();

    String getHttpRequestMethod();

    String[] getSourceIps();

    Map<String, String[]> getHttpHeaders();

    Map<String, String[]> getHttpCookies();

    Map<String, String[]> getHttpParameters();
}
