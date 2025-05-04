package com.extole.api.impl.person;

import com.extole.api.person.RiskValue;
import com.extole.common.lang.ToString;

public final class RiskValueImpl implements RiskValue {

    private final String clientId;
    private final String profileId;

    private final long riskValue;
    private final long ipRiskValue;
    private final boolean isHighRiskEmail;

    public RiskValueImpl(String clientId, String profileId, long riskValue, long ipRiskValue, boolean isHighRiskEmail) {
        this.clientId = clientId;
        this.profileId = profileId;
        this.riskValue = riskValue;
        this.ipRiskValue = ipRiskValue;
        this.isHighRiskEmail = isHighRiskEmail;
    }

    public String getClientId() {
        return clientId;
    }

    public String getProfileId() {
        return profileId;
    }

    public long getRiskValue() {
        return riskValue;
    }

    public long getIpRiskValue() {
        return ipRiskValue;
    }

    public boolean isHighRiskEmail() {
        return isHighRiskEmail;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
