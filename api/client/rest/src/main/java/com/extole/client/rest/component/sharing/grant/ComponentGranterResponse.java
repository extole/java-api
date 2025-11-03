package com.extole.client.rest.component.sharing.grant;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ComponentGranterResponse {

    private static final String GRANT_ID = "grant_id";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_NAME = "client_name";
    private static final String CLIENT_SHORT_NAME = "client_short_name";
    private static final String PREVIEW_DOMAIN = "preview_domain";
    private static final String GRANTED_DATE = "granted_date";

    private final String grantId;
    private final String clientId;
    private final String clientName;
    private final String clientShortName;
    private final String previewDomain;
    private final ZonedDateTime grantedDate;

    @JsonCreator
    public ComponentGranterResponse(
        @JsonProperty(GRANT_ID) String grantId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(CLIENT_NAME) String clientName,
        @JsonProperty(CLIENT_SHORT_NAME) String clientShortName,
        @JsonProperty(PREVIEW_DOMAIN) String previewDomain,
        @JsonProperty(GRANTED_DATE) ZonedDateTime grantedDate) {
        this.grantId = grantId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientShortName = clientShortName;
        this.previewDomain = previewDomain;
        this.grantedDate = grantedDate;
    }

    @JsonProperty(GRANT_ID)
    public String getGrantId() {
        return grantId;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(CLIENT_NAME)
    public String getClientName() {
        return clientName;
    }

    @JsonProperty(CLIENT_SHORT_NAME)
    public String getClientShortName() {
        return clientShortName;
    }

    @JsonProperty(PREVIEW_DOMAIN)
    public String getPreviewDomain() {
        return previewDomain;
    }

    @JsonProperty(GRANTED_DATE)
    public ZonedDateTime getGrantedDate() {
        return grantedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
