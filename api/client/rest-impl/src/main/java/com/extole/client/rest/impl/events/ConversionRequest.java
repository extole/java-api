package com.extole.client.rest.impl.events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableSet;
import org.springframework.util.StringUtils;

import com.extole.common.lang.ToString;
import com.extole.id.EpochPlusRandom;

@JsonAutoDetect(fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonPropertyOrder({"campaign_id", "email", "action_date", "site_id", "partner_conversion_id", "client_params"})
class ConversionRequest {
    private static final int FIVE_YEARS = 5;
    private static final int THREE_MONTHS = 3;
    private static final int HTTP_UNPROCESSABLE = 422;

    private final Long clientId;
    private final String email;
    private final String eventDate;
    private final String partnerConversionId;
    private final Map<String, String> clientParams;
    private Map<String, String> mergedParams;
    private Long actionDate;

    private static final Set<String> FILTERED_PARAMS = ImmutableSet.of("event_type", "source", "e", "f", "l",
        "via_click_id", "event_date", "partner_conversion_id", "partner_user_id");

    ConversionRequest(Long clientId, HttpServletRequest httpRequest) {
        this.clientId = clientId;
        email = httpRequest.getParameter("e");
        eventDate = httpRequest.getParameter("event_date");
        partnerConversionId = httpRequest.getParameter("partner_conversion_id");
        clientParams = httpRequest.getParameterMap().entrySet().stream()
            .filter(entry -> !FILTERED_PARAMS.contains(entry.getKey())).collect(Collectors.toMap(
                entry -> entry.getKey(), entry -> entry.getValue()[0]));
        mergedParams = new HashMap<>(clientParams);
        if (eventDate != null) {
            try {
                actionDate = parseDate(eventDate).getTime();
            } catch (ParseException ignored) {
                // handle that case in validate
            }
        }
    }

    public Long getClientId() {
        return clientId;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public String getEventDate() {
        return eventDate;
    }

    private Date parseDate(String value) throws ParseException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            return format1.parse(value);
        } catch (ParseException ignored) {
            // it's ok, try with second format
        }
        return format2.parse(value);
    }

    @Nullable
    @JsonProperty("action_date")
    public Long getActionDate() {
        return actionDate;
    }

    @JsonProperty("partner_conversion_id")
    public String getPartnerConversionId() {
        return partnerConversionId;
    }

    @JsonProperty("client_params")
    public Map<String, String> getClientParams() {
        return clientParams;
    }

    public Map<String, String> getMergedParams() {
        return mergedParams;
    }

    public void setMergedParams(Map<String, String> mergedParams) {
        this.mergedParams = mergedParams;
    }

    private void validate(String field, String value, boolean mandatory, Predicate<String> validation,
        List<String> errors) {
        if (StringUtils.isEmpty(value)) {
            if (mandatory) {
                errors.add("Missing mandatory field value for " + field);
            }
        } else if (!validation.test(value)) {
            errors.add("Invalid " + field + " value: " + value);
        }
    }

    private static final String EMAIL_REGEXP = "^([A-Za-z0-9.!#$%&'*+-/=?^_`{|}~]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)"
        + "*(\\.[A-Za-z]{2,6}))$";

    private boolean isEmail(String value) {
        if (value == null) {
            return false;
        }
        return value.matches(EMAIL_REGEXP);
    }

    private boolean isDate(String value) {
        if (value == null) {
            return false;
        }
        try {
            parseDate(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void validate() throws ConversionException {
        List<String> errors = new ArrayList<>();
        validate("email", email, true, this::isEmail, errors);
        validate("event_date", eventDate, false, this::isDate, errors);
        if (!errors.isEmpty()) {
            throw new ConversionException(String.join("; ", errors));
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @SuppressWarnings("serial")
    @JsonAutoDetect(fieldVisibility = Visibility.NONE,
        getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
    @JsonPropertyOrder({"code", "message", "params", "uniqueId"})
    class ConversionException extends Exception {
        private final long uniqueId = new EpochPlusRandom().generateId().longValue();
        private final String code;
        private final int httpCode;

        ConversionException(String message) {
            this(Status.BAD_REQUEST.getStatusCode(), "BAD REQUEST", message);
        }

        ConversionException(int httpCode, String code, String message) {
            super(message);
            this.httpCode = httpCode;
            this.code = code;
        }

        @JsonProperty("message")
        @Override
        public String getMessage() {
            return super.getMessage();
        }

        @JsonProperty("code")
        public String getCode() {
            return code;
        }

        public int getHttpCode() {
            return httpCode;
        }

        @JsonProperty("uniqueId")
        public long getUniqueId() {
            return uniqueId;
        }

        @JsonProperty("params")
        public Map<String, String> getParams() {
            Map<String, String> parameters = new HashMap<>();
            ConversionRequest request = ConversionRequest.this;
            parameters.put("client_id", String.valueOf(request.getClientId()));
            parameters.put("event_date", request.getEventDate());
            parameters.put("partner_conversion_id", request.getPartnerConversionId());
            return parameters;
        }
    }

    public void checkDateRange() throws ConversionException {
        if (actionDate == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime fiveYearsAgo = now.minusYears(FIVE_YEARS);
        LocalDateTime threeMonthsAhead = now.plusMonths(THREE_MONTHS);

        LocalDateTime localDatetime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(actionDate.longValue()), ZoneId.of("UTC"));

        if (localDatetime.isBefore(fiveYearsAgo) || localDatetime.isAfter(threeMonthsAhead)) {
            throw new ConversionException(HTTP_UNPROCESSABLE,
                "UNPROCESSABLE ENTITY",
                "The property action_date has a value of " + localDatetime
                    + " should be within the last five years and within the next 3 months.");
        }
    }

}
