package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonStatsResponse {

    private static final String JSON_AOV = "aov";
    private static final String JSON_LTV = "ltv";
    private static final String JSON_ACTIVITIES = "activities";
    private static final String JSON_TRANSACTIONS = "transactions";
    private static final String JSON_CONVERSIONS = "conversions";

    private final String aov;
    private final String ltv;
    private final long activities;
    private final long transactions;
    private final long conversions;

    @JsonCreator
    public PersonStatsResponse(
        @JsonProperty(JSON_AOV) String aov,
        @JsonProperty(JSON_LTV) String ltv,
        @JsonProperty(JSON_ACTIVITIES) long activities,
        @JsonProperty(JSON_TRANSACTIONS) long transactions,
        @JsonProperty(JSON_CONVERSIONS) long conversions) {
        this.aov = aov;
        this.ltv = ltv;
        this.activities = activities;
        this.transactions = transactions;
        this.conversions = conversions;
    }

    @JsonProperty(JSON_AOV)
    public String getAov() {
        return aov;
    }

    @JsonProperty(JSON_LTV)
    public String getLtv() {
        return ltv;
    }

    @JsonProperty(JSON_ACTIVITIES)
    public long getActivities() {
        return activities;
    }

    @JsonProperty(JSON_TRANSACTIONS)
    public long getTransactions() {
        return transactions;
    }

    @JsonProperty(JSON_CONVERSIONS)
    public long getConversions() {
        return conversions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
