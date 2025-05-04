package com.extole.reporting.rest.audience.operation.action.data.source;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class ActionAudienceOperationDataSourceRequest extends AudienceOperationDataSourceRequest {

    public static final String DATA_SOURCE_TYPE = "ACTION";

    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_COLUMNS = "event_columns";
    private static final String EVENT_DATA = "event_data";

    private final String eventName;
    private final Omissible<Set<String>> eventColumns;
    private final Omissible<Map<String, String>> eventData;

    public ActionAudienceOperationDataSourceRequest(@JsonProperty(EVENT_NAME) String eventName,
        @JsonProperty(EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @JsonProperty(EVENT_DATA) Omissible<Map<String, String>> eventData) {
        super(AudienceOperationDataSourceType.ACTION);
        this.eventName = eventName;
        this.eventColumns = eventColumns;
        this.eventData = eventData;
    }

    @JsonProperty(EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Omissible<Set<String>> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Omissible<Map<String, String>> getEventData() {
        return eventData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String eventName;
        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();

        private Builder() {

        }

        public Builder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder withEventColumns(Set<String> eventColumns) {
            this.eventColumns = Omissible.of(eventColumns);
            return this;
        }

        public Builder withEventData(Map<String, String> eventData) {
            this.eventData = Omissible.of(eventData);
            return this;
        }

        public ActionAudienceOperationDataSourceRequest build() {
            return new ActionAudienceOperationDataSourceRequest(eventName, eventColumns, eventData);
        }

    }

}
