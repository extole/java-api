package com.extole.client.rest.email;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class EmailDomainCreateRequest {

    private static final String DOMAIN = "domain";
    private static final String DKIM_CNAME_RECORDS = "dkim_cname_records";
    private static final String FORCE_SEND_FROM_EMAIL_DOMAIN = "force_send_from_email_domain";

    private final String domain;
    private final Omissible<List<CnameRecord>> dkimCnameRecords;
    private final Omissible<Boolean> forceSendFromEmailDomain;

    @JsonCreator
    public EmailDomainCreateRequest(
        @JsonProperty(DOMAIN) String domain,
        @JsonProperty(DKIM_CNAME_RECORDS) Omissible<List<CnameRecord>> dkimCnameRecords,
        @JsonProperty(FORCE_SEND_FROM_EMAIL_DOMAIN) Omissible<Boolean> forceSendFromEmailDomain) {
        this.domain = domain;
        this.dkimCnameRecords = dkimCnameRecords;
        this.forceSendFromEmailDomain = forceSendFromEmailDomain;
    }

    @JsonProperty(DOMAIN)
    public String getDomain() {
        return domain;
    }

    @JsonProperty(DKIM_CNAME_RECORDS)
    public Omissible<List<CnameRecord>> getDkimCnameRecords() {
        return dkimCnameRecords;
    }

    @JsonProperty(FORCE_SEND_FROM_EMAIL_DOMAIN)
    public Omissible<Boolean> getForceSendFromEmailDomain() {
        return forceSendFromEmailDomain;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String domain;
        private Omissible<List<CnameRecord>> dkimCnameRecords = Omissible.omitted();
        private Omissible<Boolean> forceSendFromEmailDomain = Omissible.omitted();

        private Builder() {
        }

        public Builder withDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder withCnameRecords(List<CnameRecord> cnameRecords) {
            this.dkimCnameRecords = Omissible.of(cnameRecords);
            return this;
        }

        public Builder withForceSendFromEmailDomain(boolean forceSendFromEmailDomain) {
            this.forceSendFromEmailDomain = Omissible.of(Boolean.valueOf(forceSendFromEmailDomain));
            return this;
        }

        public EmailDomainCreateRequest build() {
            return new EmailDomainCreateRequest(domain, dkimCnameRecords, forceSendFromEmailDomain);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
