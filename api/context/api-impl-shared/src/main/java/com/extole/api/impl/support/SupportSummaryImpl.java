package com.extole.api.impl.support;

import com.extole.api.support.SupportSummary;
import com.extole.common.lang.ToString;

public final class SupportSummaryImpl implements SupportSummary {
    private final String clientId;
    private final String salesforceAccountId;
    private final String slackChannelName;
    private final String externalSlackChannelName;
    private final String csmUserId;
    private final String csmEmail;
    private final String csmFirstName;
    private final String csmLastName;
    private final String supportUserId;
    private final String supportEmail;
    private final String supportFirstName;
    private final String supportLastName;

    private SupportSummaryImpl(String clientId,
        String salesforceAccountId,
        String slackChannelName,
        String externalSlackChannelName,
        String csmUserId,
        String csmEmail,
        String csmFirstName,
        String csmLastName,
        String supportUserId,
        String supportEmail,
        String supportFirstName,
        String supportLastName) {
        this.clientId = clientId;
        this.salesforceAccountId = salesforceAccountId;
        this.slackChannelName = slackChannelName;
        this.externalSlackChannelName = externalSlackChannelName;
        this.csmUserId = csmUserId;
        this.csmEmail = csmEmail;
        this.csmFirstName = csmFirstName;
        this.csmLastName = csmLastName;
        this.supportUserId = supportUserId;
        this.supportEmail = supportEmail;
        this.supportFirstName = supportFirstName;
        this.supportLastName = supportLastName;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String getSalesforceAccountId() {
        return salesforceAccountId;
    }

    @Override
    public String getSlackChannelName() {
        return slackChannelName;
    }

    @Override
    public String getExternalSlackChannelName() {
        return externalSlackChannelName;
    }

    @Override
    public String getCsmUserId() {
        return csmUserId;
    }

    @Override
    public String getCsmEmail() {
        return csmEmail;
    }

    @Override
    public String getCsmFirstName() {
        return csmFirstName;
    }

    @Override
    public String getCsmLastName() {
        return csmLastName;
    }

    @Override
    public String getSupportUserId() {
        return supportUserId;
    }

    @Override
    public String getSupportEmail() {
        return supportEmail;
    }

    @Override
    public String getSupportFirstName() {
        return supportFirstName;
    }

    @Override
    public String getSupportLastName() {
        return supportLastName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String clientId;
        private String salesforceAccountId;
        private String slackChannelName;
        private String externalSlackChannelName;
        private String csmUserId;
        private String csmEmail;
        private String csmFirstName;
        private String csmLastName;
        private String supportUserId;
        private String supportEmail;
        private String supportFirstName;
        private String supportLastName;

        private Builder() {
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withSalesforceAccountId(String salesforceAccountId) {
            this.salesforceAccountId = salesforceAccountId;
            return this;
        }

        public Builder withSlackChannelName(String slackChannelName) {
            this.slackChannelName = slackChannelName;
            return this;
        }

        public Builder withExternalSlackChannelName(String externalSlackChannelName) {
            this.externalSlackChannelName = externalSlackChannelName;
            return this;
        }

        public Builder withCsmUserId(String csmUserId) {
            this.csmUserId = csmUserId;
            return this;
        }

        public Builder withCsmEmail(String csmEmail) {
            this.csmEmail = csmEmail;
            return this;
        }

        public Builder withCsmFirstName(String csmFirstName) {
            this.csmFirstName = csmFirstName;
            return this;
        }

        public Builder withCsmLastName(String csmLastName) {
            this.csmLastName = csmLastName;
            return this;
        }

        public Builder withSupportUserId(String supportUserId) {
            this.supportUserId = supportUserId;
            return this;
        }

        public Builder withSupportEmail(String supportEmail) {
            this.supportEmail = supportEmail;
            return this;
        }

        public Builder withSupportFirstName(String supportFirstName) {
            this.supportFirstName = supportFirstName;
            return this;
        }

        public Builder withSupportLastName(String supportLastName) {
            this.supportLastName = supportLastName;
            return this;
        }

        public SupportSummaryImpl build() {
            return new SupportSummaryImpl(clientId, salesforceAccountId, slackChannelName, externalSlackChannelName,
                csmUserId, csmEmail, csmFirstName, csmLastName, supportUserId, supportEmail, supportFirstName,
                supportLastName);
        }
    }
}
