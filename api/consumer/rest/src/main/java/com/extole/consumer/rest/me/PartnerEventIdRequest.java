package com.extole.consumer.rest.me;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public final class PartnerEventIdRequest {

    private static final String QUERY_PARAMETER_PARTNER_EVENT_ID_NAME = "partner_event_id_name";
    private static final String QUERY_PARAMETER_PARTNER_EVENT_ID_VALUE = "partner_event_id_value";

    private final String name;
    private final String value;

    public PartnerEventIdRequest(@Nullable @QueryParam(QUERY_PARAMETER_PARTNER_EVENT_ID_NAME) String name,
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

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
