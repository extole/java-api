package com.extole.client.rest.person.v2;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

public final class PartnerEventIdV2Request {

    private static final String QUERY_PARAMETER_PARTNER_EVENT_ID_NAME = "partner_event_id_name";
    private static final String QUERY_PARAMETER_PARTNER_EVENT_ID_VALUE = "partner_event_id_value";

    private final String name;
    private final String value;

    public PartnerEventIdV2Request(@Nullable @QueryParam(QUERY_PARAMETER_PARTNER_EVENT_ID_NAME) String name,
        @Nullable @QueryParam(QUERY_PARAMETER_PARTNER_EVENT_ID_VALUE) String value) {
        this.name = name;
        this.value = value;
    }

    @Nullable
    @QueryParam(QUERY_PARAMETER_PARTNER_EVENT_ID_NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @QueryParam(QUERY_PARAMETER_PARTNER_EVENT_ID_VALUE)
    public String getValue() {
        return value;
    }

    public static PartnerEventIdV2Request of(String name, String value) {
        return new PartnerEventIdV2Request(name, value);
    }
}
