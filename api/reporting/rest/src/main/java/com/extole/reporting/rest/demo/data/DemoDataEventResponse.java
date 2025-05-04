package com.extole.reporting.rest.demo.data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DemoDataEventResponse {

    private static final String EVENT_TIME = "event_time";
    private static final String EVENT_NAME = "event_name";
    private static final String EMAIL = "email";
    private static final String ADVOCATE_EMAIL = "advocate.email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String CART_VALUE = "cart_value";
    private static final String PROGRAM_LABEL = "labels";
    private static final String DIMENSIONS = "dimensions";

    private final ZonedDateTime eventTime;
    private final String eventName;
    private final String email;
    private final String advocateEmail;
    private final String firstName;
    private final String lastName;
    private final BigDecimal cartValue;
    private final String programLabel;
    private final Map<String, String> dimensions;

    public DemoDataEventResponse(@JsonProperty(EVENT_TIME) ZonedDateTime eventTime,
        @JsonProperty(EVENT_NAME) String eventName,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(ADVOCATE_EMAIL) String advocateEmail,
        @Nullable @JsonProperty(FIRST_NAME) String firstName,
        @Nullable @JsonProperty(LAST_NAME) String lastName,
        @Nullable @JsonProperty(CART_VALUE) BigDecimal cartValue,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(DIMENSIONS) Map<String, String> dimensions) {
        this.eventTime = eventTime;
        this.eventName = eventName;
        this.email = email;
        this.advocateEmail = advocateEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cartValue = cartValue;
        this.programLabel = programLabel;
        this.dimensions = dimensions;
    }

    @JsonProperty(EVENT_TIME)
    public ZonedDateTime getEventTime() {
        return eventTime;
    }

    @JsonProperty(EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(ADVOCATE_EMAIL)
    public Optional<String> getAdvocateEmail() {
        return Optional.ofNullable(advocateEmail);
    }

    @JsonProperty(FIRST_NAME)
    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    @JsonProperty(LAST_NAME)
    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    @JsonProperty(CART_VALUE)
    public Optional<BigDecimal> getCartValue() {
        return Optional.ofNullable(cartValue);
    }

    @JsonProperty(PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return Optional.ofNullable(programLabel);
    }

    @JsonAnyGetter
    public Map<String, String> getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private ZonedDateTime eventTime;
        private String eventName;
        private String email;
        private String advocateEmail;
        private String firstName;
        private String lastName;
        private BigDecimal cartValue;
        private String programLabel;
        private Map<String, String> dimensions;

        public Builder() {
        }

        public Builder withEventTime(ZonedDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withAdvocateEmail(String advocateEmail) {
            this.advocateEmail = advocateEmail;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withCartValue(BigDecimal cartValue) {
            this.cartValue = cartValue;
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public Builder withDimensions(Map<String, String> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public DemoDataEventResponse build() {
            return new DemoDataEventResponse(eventTime, eventName, email, advocateEmail, firstName, lastName, cartValue,
                programLabel, dimensions);
        }
    }
}
