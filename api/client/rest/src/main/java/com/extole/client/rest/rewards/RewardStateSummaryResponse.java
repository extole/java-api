package com.extole.client.rest.rewards;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RewardStateSummaryResponse {

    private static final String DATE_FROM = "date_from";
    private static final String DATE_TO = "date_to";
    private static final String EARNED = "earned";
    private static final String FULFILLED = "fulfilled";
    private static final String SENT = "sent";
    private static final String CANCELED = "canceled";
    private static final String REDEEMED = "redeemed";
    private static final String FAILED = "failed";
    private static final String REVOKED = "revoked";

    private final ZonedDateTime dateFrom;
    private final ZonedDateTime dateTo;

    private final Integer earned;
    private final Integer fulfilled;
    private final Integer sent;
    private final Integer canceled;
    private final Integer redeemed;
    private final Integer failed;
    private final Integer revoked;

    public RewardStateSummaryResponse(@JsonProperty(DATE_FROM) ZonedDateTime dateFrom,
        @JsonProperty(DATE_TO) ZonedDateTime dateTo,
        @JsonProperty(EARNED) Integer earned,
        @JsonProperty(FULFILLED) Integer fulfilled,
        @JsonProperty(SENT) Integer sent,
        @JsonProperty(CANCELED) Integer canceled,
        @JsonProperty(REDEEMED) Integer redeemed,
        @JsonProperty(FAILED) Integer failed,
        @JsonProperty(REVOKED) Integer revoked) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.earned = earned;
        this.fulfilled = fulfilled;
        this.sent = sent;
        this.canceled = canceled;
        this.redeemed = redeemed;
        this.failed = failed;
        this.revoked = revoked;
    }

    @JsonProperty(DATE_FROM)
    public ZonedDateTime getDateFrom() {
        return dateFrom;
    }

    @JsonProperty(DATE_TO)
    public ZonedDateTime getDateTo() {
        return dateTo;
    }

    @JsonProperty(EARNED)
    public Integer getEarned() {
        return earned;
    }

    @JsonProperty(FULFILLED)
    public Integer getFulfilled() {
        return fulfilled;
    }

    @JsonProperty(SENT)
    public Integer getSent() {
        return sent;
    }

    @JsonProperty(CANCELED)
    public Integer getCanceled() {
        return canceled;
    }

    @JsonProperty(REDEEMED)
    public Integer getRedeemed() {
        return redeemed;
    }

    @JsonProperty(FAILED)
    public Integer getFailed() {
        return failed;
    }

    @JsonProperty(REVOKED)
    public Integer getRevoked() {
        return revoked;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
