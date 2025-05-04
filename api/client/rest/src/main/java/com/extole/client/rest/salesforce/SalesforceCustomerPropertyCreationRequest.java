package com.extole.client.rest.salesforce;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class SalesforceCustomerPropertyCreationRequest {
    private static final String JSON_SALESFORCE_ACCOUNT_ID = "salesforce_account_id";
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_TYPE = "type";
    private static final String JSON_VERTICAL = "vertical";
    private static final String JSON_MRR = "mrr";
    private static final String JSON_PROJECT_STATUS = "project_status";
    private static final String JSON_CSM_EMAIL = "csm_email";
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

    private final Omissible<Optional<String>> salesforceAccountId;
    private final Omissible<Optional<String>> clientId;
    private final Omissible<Optional<String>> type;
    private final Omissible<Optional<String>> vertical;
    private final Omissible<Optional<String>> mrr;
    private final Omissible<SalesforceProjectStatus> projectStatus;
    private final Omissible<Optional<String>> csmEmail;
    private final Omissible<Optional<Boolean>> usOnlySupport;
    private final Omissible<Optional<String>> riskCategory;
    private final Omissible<Optional<Boolean>> atRisk;
    private final Omissible<Optional<String>> clientPriority;
    private final Omissible<Optional<Boolean>> nvp;
    private final Omissible<Optional<Instant>> nextRenewalDate;
    private final Omissible<Optional<Instant>> initialLaunchDate;
    private final Omissible<Optional<Instant>> ssd;
    private final Omissible<Optional<String>> parentOrgName;
    private final Omissible<Optional<String>> customerJourneyStage;
    private final Omissible<Optional<Boolean>> supportOnlyServices;
    private final Omissible<Optional<String>> clientTenure;

    public SalesforceCustomerPropertyCreationRequest(
        @JsonProperty(JSON_SALESFORCE_ACCOUNT_ID) Omissible<Optional<String>> salesforceAccountId,
        @JsonProperty(JSON_CLIENT_ID) Omissible<Optional<String>> clientId,
        @JsonProperty(JSON_TYPE) Omissible<Optional<String>> type,
        @JsonProperty(JSON_VERTICAL) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> vertical,
        @JsonProperty(JSON_MRR) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> mrr,
        @JsonProperty(JSON_PROJECT_STATUS) Omissible<SalesforceProjectStatus> projectStatus,
        @JsonProperty(JSON_CSM_EMAIL) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> csmEmail,
        @JsonProperty(JSON_US_ONLY_SUPPORT) Omissible<Optional<Boolean>> usOnlySupport,
        @JsonProperty(JSON_RISK_CATEGORY) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> riskCategory,
        @JsonProperty(JSON_AT_RISK) Omissible<Optional<Boolean>> atRisk,
        @JsonProperty(JSON_CLIENT_PRIORITY) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> clientPriority,
        @JsonProperty(JSON_NVP) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<Boolean>> nvp,
        @JsonProperty(JSON_NEXT_RENEWAL_DATE) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<Instant>> nextRenewalDate,
        @JsonProperty(JSON_INITIAL_LAUNCH_DATE) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<Instant>> initialLaunchDate,
        @JsonProperty(JSON_SSD) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<Instant>> ssd,
        @JsonProperty(JSON_PARENT_ORG_NAME) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> parentOrgName,
        @JsonProperty(JSON_CUSTOMER_JOURNEY_STAGE) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> customerJourneyStage,
        @JsonProperty(JSON_SUPPORT_ONLY_SERVICES) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<Boolean>> supportOnlyServices,
        @JsonProperty(JSON_CLIENT_TENURE) @Parameter(
            description = "Empty value will remove current value") Omissible<Optional<String>> clientTenure) {
        this.salesforceAccountId = salesforceAccountId;
        this.clientId = clientId;
        this.type = type;
        this.vertical = vertical;
        this.mrr = mrr;
        this.projectStatus = projectStatus;
        this.csmEmail = csmEmail;
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
    public Omissible<Optional<String>> getSalesforceAccountId() {
        return salesforceAccountId;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public Omissible<Optional<String>> getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_TYPE)
    public Omissible<Optional<String>> getType() {
        return type;
    }

    @JsonProperty(JSON_VERTICAL)
    public Omissible<Optional<String>> getVertical() {
        return vertical;
    }

    @JsonProperty(JSON_MRR)
    public Omissible<Optional<String>> getMrr() {
        return mrr;
    }

    @JsonProperty(JSON_PROJECT_STATUS)
    public Omissible<SalesforceProjectStatus> getProjectStatus() {
        return projectStatus;
    }

    @JsonProperty(JSON_CSM_EMAIL)
    public Omissible<Optional<String>> getCsmEmail() {
        return csmEmail;
    }

    @JsonProperty(JSON_US_ONLY_SUPPORT)
    public Omissible<Optional<Boolean>> getUsOnlySupport() {
        return usOnlySupport;
    }

    @JsonProperty(JSON_RISK_CATEGORY)
    public Omissible<Optional<String>> getRiskCategory() {
        return riskCategory;
    }

    @JsonProperty(JSON_AT_RISK)
    public Omissible<Optional<Boolean>> getAtRisk() {
        return atRisk;
    }

    @JsonProperty(JSON_CLIENT_PRIORITY)
    public Omissible<Optional<String>> getClientPriority() {
        return clientPriority;
    }

    @JsonProperty(JSON_NVP)
    public Omissible<Optional<Boolean>> getNvp() {
        return nvp;
    }

    @JsonProperty(JSON_NEXT_RENEWAL_DATE)
    public Omissible<Optional<Instant>> getNextRenewalDate() {
        return nextRenewalDate;
    }

    @JsonProperty(JSON_INITIAL_LAUNCH_DATE)
    public Omissible<Optional<Instant>> getInitialLaunchDate() {
        return initialLaunchDate;
    }

    @JsonProperty(JSON_SSD)
    public Omissible<Optional<Instant>> getSsd() {
        return ssd;
    }

    @JsonProperty(JSON_PARENT_ORG_NAME)
    public Omissible<Optional<String>> getParentOrgName() {
        return parentOrgName;
    }

    @JsonProperty(JSON_CUSTOMER_JOURNEY_STAGE)
    public Omissible<Optional<String>> getCustomerJourneyStage() {
        return customerJourneyStage;
    }

    @JsonProperty(JSON_SUPPORT_ONLY_SERVICES)
    public Omissible<Optional<Boolean>> getSupportOnlyServices() {
        return supportOnlyServices;
    }

    @JsonProperty(JSON_CLIENT_TENURE)
    public Omissible<Optional<String>> getClientTenure() {
        return clientTenure;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Omissible<Optional<String>> salesforceAccountId = Omissible.omitted();
        private Omissible<Optional<String>> clientId = Omissible.omitted();
        private Omissible<Optional<String>> type = Omissible.omitted();
        private Omissible<Optional<String>> vertical = Omissible.omitted();
        private Omissible<Optional<String>> mrr = Omissible.omitted();
        private Omissible<SalesforceProjectStatus> projectStatus = Omissible.omitted();
        private Omissible<Optional<String>> csmEmail = Omissible.omitted();
        private Omissible<Optional<Boolean>> usOnlySupport = Omissible.omitted();
        private Omissible<Optional<String>> riskCategory = Omissible.omitted();
        private Omissible<Optional<Boolean>> atRisk = Omissible.omitted();
        private Omissible<Optional<String>> clientPriority = Omissible.omitted();
        private Omissible<Optional<Boolean>> nvp = Omissible.omitted();
        private Omissible<Optional<Instant>> nextRenewalDate = Omissible.omitted();
        private Omissible<Optional<Instant>> initialLaunchDate = Omissible.omitted();
        private Omissible<Optional<Instant>> ssd = Omissible.omitted();
        private Omissible<Optional<String>> parentOrgName = Omissible.omitted();
        private Omissible<Optional<String>> customerJourneyStage = Omissible.omitted();
        private Omissible<Optional<Boolean>> supportOnlyServices = Omissible.omitted();
        private Omissible<Optional<String>> clientTenure = Omissible.omitted();

        public Builder withSalesforceAccountId(String salesforceAccountId) {
            this.salesforceAccountId = Omissible.of(Optional.ofNullable(salesforceAccountId));
            return this;
        }

        public Builder withClientId(Optional<String> clientId) {
            this.clientId = Omissible.of(clientId);
            return this;
        }

        public Builder withType(Optional<String> type) {
            this.type = Omissible.of(type);
            return this;
        }

        public Builder withVertical(Optional<String> vertical) {
            this.vertical = Omissible.of(vertical);
            return this;
        }

        public Builder clearVertical() {
            this.vertical = Omissible.nullified();
            return this;
        }

        public Builder withMrr(Optional<String> mrr) {
            this.mrr = Omissible.of(mrr);
            return this;
        }

        public Builder clearMrr() {
            this.mrr = Omissible.nullified();
            return this;
        }

        public Builder withProjectStatus(SalesforceProjectStatus projectStatus) {
            this.projectStatus = Omissible.of(projectStatus);
            return this;
        }

        public Builder withCsmEmail(Optional<String> csmEmail) {
            this.csmEmail = Omissible.of(csmEmail);
            return this;
        }

        public Builder clearCsmEmail() {
            this.csmEmail = Omissible.nullified();
            return this;
        }

        public Builder withUsOnlySupport(Optional<Boolean> usOnlySupport) {
            this.usOnlySupport = Omissible.of(usOnlySupport);
            return this;
        }

        public Builder clearUsOnlySupport() {
            this.usOnlySupport = Omissible.nullified();
            return this;
        }

        public Builder withAtRisk(Optional<Boolean> atRisk) {
            this.atRisk = Omissible.of(atRisk);
            return this;
        }

        public Builder clearAtRisk() {
            this.atRisk = Omissible.nullified();
            return this;
        }

        public Builder withRiskCategory(Optional<String> riskCategory) {
            this.riskCategory = Omissible.of(riskCategory);
            return this;
        }

        public Builder clearRiskCategory() {
            this.riskCategory = Omissible.nullified();
            return this;
        }

        public Builder withClientPriority(Optional<String> clientPriority) {
            this.clientPriority = Omissible.of(clientPriority);
            return this;
        }

        public Builder clearClientPriority() {
            this.clientPriority = Omissible.nullified();
            return this;
        }

        public Builder withNvp(Optional<Boolean> nvp) {
            this.nvp = Omissible.of(nvp);
            return this;
        }

        public Builder clearNvp() {
            this.nvp = Omissible.nullified();
            return this;
        }

        public Builder withNextRenewalDate(Optional<Instant> nextRenewalDate) {
            this.nextRenewalDate = Omissible.of(nextRenewalDate);
            return this;
        }

        public Builder clearNextRenewalDate() {
            this.nextRenewalDate = Omissible.nullified();
            return this;
        }

        public Builder withInitialLaunchDate(Optional<Instant> initialLaunchDate) {
            this.initialLaunchDate = Omissible.of(initialLaunchDate);
            return this;
        }

        public Builder clearInitialLaunchDate() {
            this.initialLaunchDate = Omissible.nullified();
            return this;
        }

        public Builder withSsd(Optional<Instant> ssd) {
            this.ssd = Omissible.of(ssd);
            return this;
        }

        public Builder clearSsd() {
            this.ssd = Omissible.nullified();
            return this;
        }

        public Builder withParentOrgName(Optional<String> parentOrgName) {
            this.parentOrgName = Omissible.of(parentOrgName);
            return this;
        }

        public Builder clearParentOrgName() {
            this.parentOrgName = Omissible.nullified();
            return this;
        }

        public Builder withCustomerJourneyStage(Optional<String> customerJourneyStage) {
            this.customerJourneyStage = Omissible.of(customerJourneyStage);
            return this;
        }

        public Builder clearCustomerJourneyStage() {
            this.customerJourneyStage = Omissible.nullified();
            return this;
        }

        public Builder withSupportOnlyServices(Optional<Boolean> supportOnlyServices) {
            this.supportOnlyServices = Omissible.of(supportOnlyServices);
            return this;
        }

        public Builder clearSupportOnlyServices() {
            this.supportOnlyServices = Omissible.nullified();
            return this;
        }

        public Builder withClientTenure(Optional<String> clientTenure) {
            this.clientTenure = Omissible.of(clientTenure);
            return this;
        }

        public Builder clearClientTenure() {
            this.clientTenure = Omissible.nullified();
            return this;
        }

        public SalesforceCustomerPropertyCreationRequest build() {
            return new SalesforceCustomerPropertyCreationRequest(salesforceAccountId, clientId, type, vertical, mrr,
                projectStatus, csmEmail, usOnlySupport, riskCategory, atRisk,
                clientPriority, nvp, nextRenewalDate, initialLaunchDate, ssd, parentOrgName, customerJourneyStage,
                supportOnlyServices, clientTenure);
        }
    }
}
