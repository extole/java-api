package com.extole.client.rest.email;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class EmailDomainValidationResponse {
    private static final String DOMAIN_VALIDATION_STATUS = "domain_validation_status";
    private static final String EMAIL_DOMAIN = "email_domain";

    private static final String SPF = "spf";
    private static final String DKIM = "dkim";
    private static final String DMARC = "dmarc";
    private static final String A = "a";
    private static final String MX = "mx";

    private final DomainValidationStatus domainValidationStatus;
    private final String emailDomain;
    private final DnsRecordVerificationResponse spf;
    private final List<DnsRecordVerificationResponse> dkim;
    private final DnsRecordVerificationResponse dmarc;

    private final DnsRecordVerificationResponse a;
    private final DnsRecordVerificationResponse mx;

    public EmailDomainValidationResponse(
        @JsonProperty(DOMAIN_VALIDATION_STATUS) DomainValidationStatus domainValidationStatus,
        @JsonProperty(EMAIL_DOMAIN) String emailDomain,
        @JsonProperty(SPF) DnsRecordVerificationResponse spf,
        @JsonProperty(DKIM) List<DnsRecordVerificationResponse> dkim,
        @JsonProperty(DMARC) DnsRecordVerificationResponse dmarc,
        @JsonProperty(A) DnsRecordVerificationResponse a,
        @JsonProperty(MX) DnsRecordVerificationResponse mx) {
        this.domainValidationStatus = domainValidationStatus;
        this.emailDomain = emailDomain;
        this.spf = spf;
        this.dkim = dkim;
        this.dmarc = dmarc;
        this.a = a;
        this.mx = mx;
    }

    @JsonProperty(DOMAIN_VALIDATION_STATUS)
    public DomainValidationStatus getDomainValidationStatus() {
        return domainValidationStatus;
    }

    @JsonProperty(EMAIL_DOMAIN)
    public String getEmailDomain() {
        return emailDomain;
    }

    @JsonProperty(SPF)
    public DnsRecordVerificationResponse getSpf() {
        return spf;
    }

    @JsonProperty(DKIM)
    public List<DnsRecordVerificationResponse> getDkim() {
        return dkim;
    }

    @JsonProperty(DMARC)
    public DnsRecordVerificationResponse getDmarc() {
        return dmarc;
    }

    @JsonProperty(A)
    public DnsRecordVerificationResponse getA() {
        return a;
    }

    @JsonProperty(MX)
    public DnsRecordVerificationResponse getMx() {
        return mx;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
