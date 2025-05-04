package com.extole.api.person;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface RiskValue {

    long getRiskValue();

    long getIpRiskValue();

    boolean isHighRiskEmail();
}
