package com.extole.client.rest.events.v4;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientConsumerEventV4Request {
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_EVENT_TIME = "event_time";
    private static final String JSON_PERSON_ID = "person_id";
    private static final String JSON_ACCEPT_MIME_TYPES = "accept_mime_types";
    private static final String JSON_PARAMETERS = "parameters";
    private static final String JSON_DATA = "data";

    public static final String DATA_EMAIL = "email";
    public static final String DATA_PARTNER_USER_ID = "partner_user_id";
    public static final String DATA_SOURCE = "source";
    public static final String DATA_PARTNER_CONVERSION_ID = "partner_conversion_id";
    public static final String DATA_CLIENT_DOMAIN_NAME = "client_domain_name";

    /**
     * @deprecated use separate person_id field instead
     */
    @Deprecated // TBD - OPEN TICKET
    public static final String DATA_PERSON_ID = "person_id";
    /**
     * @deprecated use separate event_time filed instead
     */
    @Deprecated // TBD - OPEN TICKET
    public static final String DATA_EVENT_DATE = "event_date";
    @Deprecated // TBD - OPEN TICKET
    public static final String DATA_SITE_HOST_NAME = "site_name";
    @Deprecated // TBD - OPEN TICKET
    public static final String DATA_CLICK_ID = "via_click_id";

    private final String eventName;
    private final ZonedDateTime eventTime;
    private final String personId;
    private final List<String> acceptMimeTypes;
    private final Map<String, String> data;

    public ClientConsumerEventV4Request(
        @JsonProperty(JSON_EVENT_NAME) String eventName,
        @Nullable @JsonProperty(JSON_EVENT_TIME) ZonedDateTime eventTime,
        @Nullable @JsonProperty(JSON_PERSON_ID) String personId,
        @Nullable @JsonProperty(JSON_ACCEPT_MIME_TYPES) List<String> acceptMimeTypes,
        @Nullable @JsonProperty(JSON_PARAMETERS) Map<String, String> parameters,
        @Nullable @JsonProperty(JSON_DATA) Map<String, String> data) {
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.personId = personId;
        this.acceptMimeTypes =
            acceptMimeTypes != null ? Collections.unmodifiableList(acceptMimeTypes) : Collections.emptyList();
        this.data = data != null ? Collections.unmodifiableMap(data)
            : (parameters != null ? Collections.unmodifiableMap(parameters) : Collections.emptyMap());
    }

    @JsonProperty(JSON_EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_EVENT_TIME)
    public Optional<ZonedDateTime> getEventTime() {
        return Optional.ofNullable(eventTime);
    }

    @Nullable
    @JsonProperty(JSON_PERSON_ID)
    public String getPersonId() {
        return personId;
    }

    @JsonProperty(JSON_ACCEPT_MIME_TYPES)
    public List<String> getAcceptMimeTypes() {
        return acceptMimeTypes;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static ClientConsumerEventRequestBuilder builder() {
        return new ClientConsumerEventRequestBuilder();
    }

    public static final class ClientConsumerEventRequestBuilder {
        private String eventName;
        private ZonedDateTime eventTime;
        private String personId;
        private List<String> acceptMimeTypes;
        private Map<String, String> data;

        private ClientConsumerEventRequestBuilder() {
        }

        public ClientConsumerEventRequestBuilder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public ClientConsumerEventRequestBuilder withEventTime(ZonedDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public ClientConsumerEventRequestBuilder withPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public ClientConsumerEventRequestBuilder withAcceptMimeTypes(List<String> acceptMimeTypes) {
            this.acceptMimeTypes = acceptMimeTypes;
            return this;
        }

        public ClientConsumerEventRequestBuilder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public ClientConsumerEventV4Request build() {
            return new ClientConsumerEventV4Request(eventName, eventTime, personId, acceptMimeTypes, null, data);
        }
    }
}
