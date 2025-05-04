package com.extole.common.rest.exception;

import java.util.Map;

// TODO endpoints declare/document all potential 4xx errors ENG-16059
public class WebApplicationRestRuntimeException extends ExtoleRestRuntimeException {
    public static final ErrorCode<WebApplicationRestRuntimeException> UNAUTHORIZED =
        new ErrorCode<>("method_unauthorized", 401, "Unauthorized access to this endpoint");
    public static final ErrorCode<WebApplicationRestRuntimeException> FORBIDDEN =
        new ErrorCode<>("method_unauthorized", 403, "Access forbidden to this endpoint");
    public static final ErrorCode<WebApplicationRestRuntimeException> NOT_FOUND =
        new ErrorCode<>("method_not_found", 404, "Unable to find specified endpoint");
    public static final ErrorCode<WebApplicationRestRuntimeException> NOT_ALLOWED =
        new ErrorCode<>("method_not_allowed", 405, "Access not allowed to this endpoint");
    public static final ErrorCode<WebApplicationRestRuntimeException> UNSUPPORTED_MEDIA_TYPE =
        new ErrorCode<>("unsupported_media_type", 415, "Request had an unsupported or no media type");
    public static final ErrorCode<WebApplicationRestRuntimeException> UNEXPECTED_REQUEST =
        new ErrorCode<>("unexpected_request", 400, "Received unexpected request");
    public static final ErrorCode<WebApplicationRestRuntimeException> MISSING_REQUEST_BODY =
        new ErrorCode<>("missing_request_body", 400, "Missing request body");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_PARAMETER =
        new ErrorCode<>("invalid_parameter", 400, "Parameter is invalid.");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_JSON =
        new ErrorCode<>("invalid_json", 400, "JSON is invalid", "location");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_JSON_NON_PARSEABLE =
        new ErrorCode<>("invalid_json_non_parseable", 400, "Error occurred on json parse",
            "detailed_message", "location");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_JSON_MALFORMED =
        new ErrorCode<>("invalid_json_malformed", 400, "Invalid json format",
            "invalid_property", "invalid_value", "location");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_JSON_UNKNOWN_TYPE_ID =
        new ErrorCode<>("invalid_json_unknown_type_id", 400, "Type id is not valid",
            "invalid_property", "location");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_JSON_UNRECOGNIZED_PROPERTY =
        new ErrorCode<>("invalid_json_unrecognized_property", 400, "Unrecognized property found",
            "unrecognized_property", "known_properties", "location");
    public static final ErrorCode<WebApplicationRestRuntimeException> BINDING_ERROR =
        new ErrorCode<>("binding_error", 400, "Argument is not of the expected type", "argument");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_TIMEZONE = new ErrorCode<>(
        "timezone_invalid", 400, "Timezone is invalid or not ISO-8601 compliant", "argument", "value");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_DATE_TIME = new ErrorCode<>(
        "date_time_invalid", 400, "Provided string is not a valid date time or is not ISO 8601 compliant", "value");
    public static final ErrorCode<WebApplicationRestRuntimeException> UNSUPPORTED_ENUM_TYPE = new ErrorCode<>(
        "unsupported_type", 400, "Provided string is not a supported value for the enum type",
        "value", "allowed_values");
    public static final ErrorCode<WebApplicationRestRuntimeException> TOO_MANY_REQUESTS = new ErrorCode<>(
        "too_many_requests", 429, "The server is unable to process your request at the moment, please retry later.");
    public static final ErrorCode<WebApplicationRestRuntimeException> INVALID_URI = new ErrorCode<>(
        "invalid_uri", 400, "Invalid URI.", "incoming_url");
    public static final ErrorCode<WebApplicationRestRuntimeException> NOT_ACCEPTABLE = new ErrorCode<>(
        "not_acceptable", 406, "Not Acceptable");

    public WebApplicationRestRuntimeException(String uniqueId, ErrorCode<FatalRestRuntimeException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
