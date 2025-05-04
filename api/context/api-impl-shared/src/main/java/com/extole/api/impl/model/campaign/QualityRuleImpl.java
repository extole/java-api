package com.extole.api.impl.model.campaign;

import java.util.Map;
import java.util.stream.Collectors;

import com.extole.api.model.campaign.QualityRule;

public final class QualityRuleImpl implements QualityRule {
    private final com.extole.model.entity.QualityRule qualityRule;

    public QualityRuleImpl(com.extole.model.entity.QualityRule qualityRule) {
        this.qualityRule = qualityRule;
    }

    @Override
    public String getId() {
        return qualityRule.getId().getValue();
    }

    @Override
    public String getRuleType() {
        return qualityRule.getRuleType().name();
    }

    @Override
    public boolean getEnabled() {
        return qualityRule.getEnabled();
    }

    @Override
    public String getCreatedDate() {
        return qualityRule.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return qualityRule.getUpdatedDate().toString();
    }

    @Override
    public Map<String, String[]> getProperties() {
        return qualityRule.getProperties().entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(e -> e.getKey(), e -> e.getValue().toArray(String[]::new)));
    }

    @Override
    public String[] getActionTypes() {
        return qualityRule.getActionTypes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }
}
