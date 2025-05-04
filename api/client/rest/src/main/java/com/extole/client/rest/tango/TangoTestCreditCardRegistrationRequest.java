package com.extole.client.rest.tango;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TangoTestCreditCardRegistrationRequest {

    private static final String LABEL = "label";
    private static final String BILLING_ADDRESS_FIRST_NAME = "billing_address_first_name";
    private static final String BILLING_ADDRESS_LAST_NAME = "billing_address_last_name";
    private static final String BILLING_ADDRESS_LINE_1 = "billing_address_line_1";
    private static final String BILLING_ADDRESS_LINE_2 = "billing_address_line_2";
    private static final String BILLING_ADDRESS_CITY = "billing_address_city";
    private static final String BILLING_ADDRESS_STATE = "billing_address_state";
    private static final String BILLING_ADDRESS_POSTAL_CODE = "billing_address_postal_code";
    private static final String BILLING_ADDRESS_COUNTRY = "billing_address_country";
    private static final String BILLING_ADDRESS_EMAIL = "billing_address_email";
    private static final String CONTACTS = "contacts";

    private final String label;
    private final String billingAddressFirstName;
    private final String billingAddressLastName;
    private final String billingAddressLine1;
    private final String billingAddressLine2;
    private final String billingAddressCity;
    private final String billingAddressState;
    private final String billingAddressPostalCode;
    private final String billingAddressCountry;
    private final String billingAddressEmail;
    private final List<TangoCreditCardContactInformation> contacts;

    public TangoTestCreditCardRegistrationRequest(
        @JsonProperty(LABEL) String label,
        @JsonProperty(BILLING_ADDRESS_FIRST_NAME) String billingAddressFirstName,
        @JsonProperty(BILLING_ADDRESS_LAST_NAME) String billingAddressLastName,
        @JsonProperty(BILLING_ADDRESS_LINE_1) String billingAddressLine1,
        @JsonProperty(BILLING_ADDRESS_LINE_2) String billingAddressLine2,
        @JsonProperty(BILLING_ADDRESS_CITY) String billingAddressCity,
        @JsonProperty(BILLING_ADDRESS_STATE) String billingAddressState,
        @JsonProperty(BILLING_ADDRESS_POSTAL_CODE) String billingAddressPostalCode,
        @JsonProperty(BILLING_ADDRESS_COUNTRY) String billingAddressCountry,
        @JsonProperty(BILLING_ADDRESS_EMAIL) String billingAddressEmail,
        @JsonProperty(CONTACTS) List<TangoCreditCardContactInformation> contacts) {
        this.label = label;
        this.billingAddressFirstName = billingAddressFirstName;
        this.billingAddressLastName = billingAddressLastName;
        this.billingAddressLine1 = billingAddressLine1;
        this.billingAddressLine2 = billingAddressLine2;
        this.billingAddressCity = billingAddressCity;
        this.billingAddressState = billingAddressState;
        this.billingAddressPostalCode = billingAddressPostalCode;
        this.billingAddressCountry = billingAddressCountry;
        this.billingAddressEmail = billingAddressEmail;
        this.contacts = Optional.ofNullable(contacts).orElse(Collections.emptyList());
    }

    @JsonProperty(LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(BILLING_ADDRESS_FIRST_NAME)
    public String getBillingAddressFirstName() {
        return billingAddressFirstName;
    }

    @JsonProperty(BILLING_ADDRESS_LAST_NAME)
    public String getBillingAddressLastName() {
        return billingAddressLastName;
    }

    @JsonProperty(BILLING_ADDRESS_LINE_1)
    public String getBillingAddressLine1() {
        return billingAddressLine1;
    }

    @JsonProperty(BILLING_ADDRESS_LINE_2)
    public String getBillingAddressLine2() {
        return billingAddressLine2;
    }

    @JsonProperty(BILLING_ADDRESS_CITY)
    public String getBillingAddressCity() {
        return billingAddressCity;
    }

    @JsonProperty(BILLING_ADDRESS_STATE)
    public String getBillingAddressState() {
        return billingAddressState;
    }

    @JsonProperty(BILLING_ADDRESS_POSTAL_CODE)
    public String getBillingAddressPostalCode() {
        return billingAddressPostalCode;
    }

    @JsonProperty(BILLING_ADDRESS_COUNTRY)
    public String getBillingAddressCountry() {
        return billingAddressCountry;
    }

    @JsonProperty(BILLING_ADDRESS_EMAIL)
    public String getBillingAddressEmail() {
        return billingAddressEmail;
    }

    @JsonProperty(CONTACTS)
    public List<TangoCreditCardContactInformation> getContacts() {
        return contacts;
    }

}
