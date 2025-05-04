package com.extole.client.rest.tango;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TangoCreditCardResponse {

    private static final String ID = "id";
    private static final String ACCOUNT_ID = "account_id";
    private static final String ACTIVATION_DATE = "activation_date";
    private static final String CREATED_DATE = "created_date";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String EXPIRATION_DATE = "expiration_date";
    private static final String LABEL = "label";
    private static final String LAST_FOUR_DIGITS = "last_four_digits";
    private static final String STATUS = "status";

    private final String id;
    private final String accountIdentifier;
    private final String activationDate;
    private final String createdDate;
    private final String customerIdentifier;
    private final String exprationDate;
    private final String label;
    private final String lastFourDigits;
    private final TangoCreditCardStatus status;

    @JsonCreator
    public TangoCreditCardResponse(@JsonProperty(ID) String id, @JsonProperty(ACCOUNT_ID) String accountIdentifier,
        @JsonProperty(ACTIVATION_DATE) String activationDate, @JsonProperty(CREATED_DATE) String createdDate,
        @JsonProperty(CUSTOMER_ID) String customerIdentifier, @JsonProperty(EXPIRATION_DATE) String exprationDate,
        @JsonProperty(LABEL) String label, @JsonProperty(LAST_FOUR_DIGITS) String lastFourDigits,
        @JsonProperty(STATUS) TangoCreditCardStatus status) {
        this.id = id;
        this.accountIdentifier = accountIdentifier;
        this.activationDate = activationDate;
        this.createdDate = createdDate;
        this.customerIdentifier = customerIdentifier;
        this.exprationDate = exprationDate;
        this.label = label;
        this.lastFourDigits = lastFourDigits;
        this.status = status;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(ACCOUNT_ID)
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    @JsonProperty(ACTIVATION_DATE)
    public String getActivationDate() {
        return activationDate;
    }

    @JsonProperty(CREATED_DATE)
    public String getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(CUSTOMER_ID)
    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    @JsonProperty(EXPIRATION_DATE)
    public String getExprationDate() {
        return exprationDate;
    }

    @JsonProperty(LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(LAST_FOUR_DIGITS)
    public String getLastFourDigits() {
        return lastFourDigits;
    }

    @JsonProperty(STATUS)
    public String getStatus() {
        return status.toString();
    }
}
