package com.extole.client.rest.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.extole.common.lang.ToString;

public class DnsRecordVerificationResponse {
    private static final String DOMAIN_VALIDATION_STATUS = "domain_validation_status";
    private static final String RECORD = "record";
    private static final String REASON = "reason";

    private final DomainValidationStatus domainValidationStatus;
    private final String record;
    private final String reason;

    public DnsRecordVerificationResponse(
        @JsonProperty(DOMAIN_VALIDATION_STATUS) DomainValidationStatus domainValidationStatus,
        @JsonProperty(RECORD) String record,
        @JsonProperty(REASON) String reason) {
        this.domainValidationStatus = domainValidationStatus;
        this.record = record;
        this.reason = reason;
    }

    @JsonProperty(DOMAIN_VALIDATION_STATUS)
    public DomainValidationStatus getDomainValidationStatus() {
        return domainValidationStatus;
    }

    @JsonProperty(RECORD)
    public String getRecord() {
        return record;
    }

    @JsonPropertyOrder(REASON)
    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
