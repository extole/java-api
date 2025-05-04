package com.extole.client.rest.events.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientConsumerEventV4RestException extends ExtoleRestException {
    public static final ErrorCode<ClientConsumerEventV4RestException> MISSING_EVENT_NAME =
        new ErrorCode<>("missing_event_name", 400, "Missing event name", "client_id");

    public static final ErrorCode<ClientConsumerEventV4RestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 400, "Person Not Found", "client_id", "person_id");

    public static final ErrorCode<ClientConsumerEventV4RestException> INVALID_SITE =
        new ErrorCode<>("invalid_site", 400, "Invalid Site", "client_id", "site_host_name");

    public static final ErrorCode<ClientConsumerEventV4RestException> INVALID_CLIENT_DOMAIN =
        new ErrorCode<>("invalid_client_domain", 400, "Invalid client domain", "client_id", "client_domain_name");

    public static final ErrorCode<ClientConsumerEventV4RestException> CLIENT_HAS_NO_DOMAIN =
        new ErrorCode<>("client_has_no_domain", 400, "Client has no domain defined", "client_id");

    public static final ErrorCode<ClientConsumerEventV4RestException> INVALID_EVENT_DATE_FORMAT =
        new ErrorCode<>("invalid_event_date_format", 400, "Invalid event date format. Expected: ISO8601 format",
            "client_id", "event_date");

    public static final ErrorCode<ClientConsumerEventV4RestException> INVALID_TIME_ZONE =
        new ErrorCode<>("invalid_time_zone", 400, "Invalid time zone.",
            "time_zone");

    public ClientConsumerEventV4RestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
