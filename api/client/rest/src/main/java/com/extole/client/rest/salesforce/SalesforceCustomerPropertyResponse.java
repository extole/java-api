package com.extole.client.rest.salesforce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.Client;
import com.extole.client.rest.client.ClientType;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class SalesforceCustomerPropertyResponse {

    private static final String JSON_SALESFORCE_ACCOUNT_ID = "salesforce_account_id";
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_TYPE = "type";
    private static final String JSON_VERTICAL = "vertical";
    private static final String JSON_MRR = "mrr";
    private static final String JSON_PROJECT_STATUSES = "project_statuses";
    private static final String JSON_CSM_ID = "csm_id";
    private static final String JSON_CSM_EMAIL = "csm_email";
    private static final String JSON_SUPPORT_ENGINEER_ID = "support_engineer_id";
    private static final String JSON_SUPPORT_ENGINEER_EMAIL = "support_engineer_email";
    private static final String JSON_SUPPORT_ENGINEER_NAME = "support_engineer_name";
    private static final String JSON_US_ONLY_SUPPORT = "us_only_support";
    private static final String JSON_RISK_CATEGORY = "risk_category";
    private static final String JSON_AT_RISK = "at_risk";
    private static final String JSON_CLIENT_PRIORITY = "client_priority";
    private static final String JSON_NVP = "nvp";
    private static final String JSON_NEXT_RENEWAL_DATE = "next_renewal_date";
    private static final String JSON_INITIAL_LAUNCH_DATE = "initial_launch_date";
    private static final String JSON_SSD = "ssd";
    private static final String JSON_PARENT_ORG_NAME = "parent_org_name";
    private static final String JSON_CUSTOMER_JOURNEY_STAGE = "customer_journey_stage";
    private static final String JSON_SUPPORT_ONLY_SERVICES = "support_only_services";
    private static final String JSON_CLIENT_TENURE = "client_tenure";

    private final Optional<String> salesforceAccountId;
    private final Id<Client> clientId;
    private final Optional<ClientType> type;
    private final Optional<String> vertical;
    private final Optional<BigDecimal> mrr;
    private final List<SalesforceProjectStatus> projectStatuses;
    private final Optional<String> csmId;
    private final Optional<String> csmEmail;
    private final Optional<String> supportEngineerId;
    private final Optional<String> supportEngineerEmail;
    private final Optional<String> supportEngineerName;
    private final Optional<Boolean> usOnlySupport;
    private final Optional<String> riskCategory;
    private final Optional<Boolean> atRisk;
    private final Optional<String> clientPriority;
    private final Optional<Boolean> nvp;
    private final Optional<Instant> nextRenewalDate;
    private final Optional<Instant> initialLaunchDate;
    private final Optional<Instant> ssd;
    private final Optional<String> parentOrgName;
    private final Optional<String> customerJourneyStage;
    private final Optional<Boolean> supportOnlyServices;
    private final Optional<String> clientTenure;

    public SalesforceCustomerPropertyResponse(
        @JsonProperty(JSON_SALESFORCE_ACCOUNT_ID) Optional<String> salesforceAccountId,
        @JsonProperty(JSON_CLIENT_ID) Id<Client> clientId,
        @JsonProperty(JSON_TYPE) Optional<ClientType> type,
        @JsonProperty(JSON_VERTICAL) Optional<String> vertical,
        @JsonProperty(JSON_MRR) Optional<BigDecimal> mrr,
        @JsonProperty(JSON_PROJECT_STATUSES) List<SalesforceProjectStatus> projectStatuses,
        @JsonProperty(JSON_CSM_ID) Optional<String> csmId,
        @JsonProperty(JSON_CSM_EMAIL) Optional<String> csmEmail,
        @JsonProperty(JSON_SUPPORT_ENGINEER_ID) Optional<String> supportEngineerId,
        @JsonProperty(JSON_SUPPORT_ENGINEER_EMAIL) Optional<String> supportEngineerEmail,
        @JsonProperty(JSON_SUPPORT_ENGINEER_NAME) Optional<String> supportEngineerName,
        @JsonProperty(JSON_US_ONLY_SUPPORT) Optional<Boolean> usOnlySupport,
        @JsonProperty(JSON_RISK_CATEGORY) Optional<String> riskCategory,
        @JsonProperty(JSON_AT_RISK) Optional<Boolean> atRisk,
        @JsonProperty(JSON_CLIENT_PRIORITY) Optional<String> clientPriority,
        @JsonProperty(JSON_NVP) Optional<Boolean> nvp,
        @JsonProperty(JSON_NEXT_RENEWAL_DATE) Optional<Instant> nextRenewalDate,
        @JsonProperty(JSON_INITIAL_LAUNCH_DATE) Optional<Instant> initialLaunchDate,
        @JsonProperty(JSON_SSD) Optional<Instant> ssd,
        @JsonProperty(JSON_PARENT_ORG_NAME) Optional<String> parentOrgName,
        @JsonProperty(JSON_CUSTOMER_JOURNEY_STAGE) Optional<String> customerJourneyStage,
        @JsonProperty(JSON_SUPPORT_ONLY_SERVICES) Optional<Boolean> supportOnlyServices,
        @JsonProperty(JSON_CLIENT_TENURE) Optional<String> clientTenure) {
        this.salesforceAccountId = salesforceAccountId;
        this.clientId = clientId;
        this.type = type;
        this.vertical = vertical;
        this.mrr = mrr;
        this.projectStatuses = projectStatuses;
        this.csmId = csmId;
        this.csmEmail = csmEmail;
        this.supportEngineerId = supportEngineerId;
        this.supportEngineerEmail = supportEngineerEmail;
        this.supportEngineerName = supportEngineerName;
        this.usOnlySupport = usOnlySupport;
        this.riskCategory = riskCategory;
        this.atRisk = atRisk;
        this.clientPriority = clientPriority;
        this.nvp = nvp;
        this.nextRenewalDate = nextRenewalDate;
        this.initialLaunchDate = initialLaunchDate;
        this.ssd = ssd;
        this.parentOrgName = parentOrgName;
        this.customerJourneyStage = customerJourneyStage;
        this.supportOnlyServices = supportOnlyServices;
        this.clientTenure = clientTenure;
    }

    @JsonProperty(JSON_SALESFORCE_ACCOUNT_ID)
    public Optional<String> getSalesforceAccountId() {
        return salesforceAccountId;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public Id<Client> getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_TYPE)
    public Optional<ClientType> getType() {
        return type;
    }

    @JsonProperty(JSON_VERTICAL)
    public Optional<String> getVertical() {
        return vertical;
    }

    @JsonProperty(JSON_MRR)
    public Optional<BigDecimal> getMrr() {
        return mrr;
    }

    @JsonProperty(JSON_PROJECT_STATUSES)
    public List<SalesforceProjectStatus> getProjectStatuses() {
        return projectStatuses;
    }

    @JsonProperty(JSON_CSM_ID)
    public Optional<String> getCsmId() {
        return csmId;
    }

    @JsonProperty(JSON_CSM_EMAIL)
    public Optional<String> getCsmEmail() {
        return csmEmail;
    }

    @JsonProperty(JSON_SUPPORT_ENGINEER_ID)
    public Optional<String> getSupportEngineerId() {
        return supportEngineerId;
    }

    @JsonProperty(JSON_SUPPORT_ENGINEER_EMAIL)
    public Optional<String> getSupportEngineerEmail() {
        return supportEngineerEmail;
    }

    @JsonProperty(JSON_SUPPORT_ENGINEER_NAME)
    public Optional<String> getSupportEngineerName() {
        return supportEngineerName;
    }

    @JsonProperty(JSON_US_ONLY_SUPPORT)
    public Optional<Boolean> getUsOnlySupport() {
        return usOnlySupport;
    }

    @JsonProperty(JSON_RISK_CATEGORY)
    public Optional<String> getRiskCategory() {
        return riskCategory;
    }

    @JsonProperty(JSON_AT_RISK)
    public Optional<Boolean> getAtRisk() {
        return atRisk;
    }

    @JsonProperty(JSON_CLIENT_PRIORITY)
    public Optional<String> getClientPriority() {
        return clientPriority;
    }

    @JsonProperty(JSON_NVP)
    public Optional<Boolean> getNvp() {
        return nvp;
    }

    @JsonProperty(JSON_NEXT_RENEWAL_DATE)
    public Optional<Instant> getNextRenewalDate() {
        return nextRenewalDate;
    }

    @JsonProperty(JSON_INITIAL_LAUNCH_DATE)
    public Optional<Instant> getInitialLaunchDate() {
        return initialLaunchDate;
    }

    @JsonProperty(JSON_SSD)
    public Optional<Instant> getSsd() {
        return ssd;
    }

    @JsonProperty(JSON_PARENT_ORG_NAME)
    public Optional<String> getParentOrgName() {
        return parentOrgName;
    }

    @JsonProperty(JSON_CUSTOMER_JOURNEY_STAGE)
    public Optional<String> getCustomerJourneyStage() {
        return customerJourneyStage;
    }

    @JsonProperty(JSON_SUPPORT_ONLY_SERVICES)
    public Optional<Boolean> getSupportOnlyServices() {
        return supportOnlyServices;
    }

    @JsonProperty(JSON_CLIENT_TENURE)
    public Optional<String> getClientTenure() {
        return clientTenure;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
