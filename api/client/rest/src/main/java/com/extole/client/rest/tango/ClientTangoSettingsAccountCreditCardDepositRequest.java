package com.extole.client.rest.tango;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientTangoSettingsAccountCreditCardDepositRequest {

    private static final String AMOUNT = "amount";
    private static final String CREDIT_CARD_ID = "credit_card_id";

    private final BigDecimal amount;
    private final String creditCardId;

    public ClientTangoSettingsAccountCreditCardDepositRequest(@JsonProperty(AMOUNT) BigDecimal amount,
        @JsonProperty(CREDIT_CARD_ID) String creditCardId) {
        this.amount = amount;
        this.creditCardId = creditCardId;
    }

    @JsonProperty(AMOUNT)
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty(CREDIT_CARD_ID)
    public String getCreditCardId() {
        return creditCardId;
    }

}
