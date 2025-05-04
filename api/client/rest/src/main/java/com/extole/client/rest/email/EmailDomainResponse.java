package com.extole.client.rest.email;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class EmailDomainResponse {
    private static final String ID = "id";
    private static final String DOMAIN = "domain";
    private static final String DKIM_CNAME_RECORDS = "dkim_cname_records";
    private static final String FORCE_SEND_FROM_EMAIL_DOMAIN = "force_send_from_email_domain";

    private final String id;
    private final String domain;
    private final List<CnameRecord> dkimCnameRecords;
    private final boolean forceSendFromEmailDomain;

    @JsonCreator
    public EmailDomainResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(DOMAIN) String domain,
        @JsonProperty(DKIM_CNAME_RECORDS) List<CnameRecord> dkimCnameRecords,
        @JsonProperty(FORCE_SEND_FROM_EMAIL_DOMAIN) boolean forceSendFromEmailDomain) {
        this.id = id;
        this.domain = domain;
        this.dkimCnameRecords = dkimCnameRecords;
        this.forceSendFromEmailDomain = forceSendFromEmailDomain;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(DOMAIN)
    public String getDomain() {
        return domain;
    }

    @JsonProperty(FORCE_SEND_FROM_EMAIL_DOMAIN)
    public boolean isForceSendFromEmailDomain() {
        return forceSendFromEmailDomain;
    }

    @JsonProperty(DKIM_CNAME_RECORDS)
    public List<CnameRecord> getDkimCnameRecords() {
        return dkimCnameRecords;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
