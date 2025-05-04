package com.extole.client.rest.program;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.email.DomainValidationStatus;
import com.extole.common.lang.ToString;

public class ProgramDomainValidationResponse {
    private static final String DOMAIN_VALIDATION_STATUS = "domain_validation_status";
    private static final String PROGRAM_DOMAIN = "program_domain";
    private static final String CANONICAL_NAME = "canonical_name";

    private final DomainValidationStatus domainValidationStatus;
    private final String programDomain;
    private final String canonicalName;

    public ProgramDomainValidationResponse(
        @JsonProperty(DOMAIN_VALIDATION_STATUS) DomainValidationStatus domainValidationStatus,
        @JsonProperty(PROGRAM_DOMAIN) String programDomain,
        @Nullable @JsonProperty(CANONICAL_NAME) String canonicalName) {
        this.domainValidationStatus = domainValidationStatus;
        this.programDomain = programDomain;
        this.canonicalName = canonicalName;
    }

    @JsonProperty(DOMAIN_VALIDATION_STATUS)
    public DomainValidationStatus getDomainValidationStatus() {
        return domainValidationStatus;
    }

    @JsonProperty(PROGRAM_DOMAIN)
    public String getProgramDomain() {
        return programDomain;
    }

    @JsonProperty(CANONICAL_NAME)
    public Optional<String> getCanonicalName() {
        return Optional.ofNullable(canonicalName);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
