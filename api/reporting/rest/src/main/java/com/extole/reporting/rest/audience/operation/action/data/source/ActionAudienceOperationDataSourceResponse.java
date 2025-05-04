package com.extole.reporting.rest.audience.operation.action.data.source;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class ActionAudienceOperationDataSourceResponse extends AudienceOperationDataSourceResponse {

    public static final String DATA_SOURCE_TYPE = "ACTION";

    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_COLUMNS = "event_columns";
    private static final String EVENT_DATA = "event_data";

    private final String eventName;
    private final Set<String> eventColumns;
    private final Map<String, String> eventData;

    public ActionAudienceOperationDataSourceResponse(
        @JsonProperty(EVENT_NAME) String eventName,
        @JsonProperty(EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(EVENT_DATA) Map<String, String> eventData) {
        super(AudienceOperationDataSourceType.ACTION);
        this.eventName = eventName;
        this.eventColumns = ImmutableSet.copyOf(eventColumns);
        this.eventData = ImmutableMap.copyOf(eventData);
    }

    @JsonProperty(EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Set<String> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Map<String, String> getEventData() {
        return eventData;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
