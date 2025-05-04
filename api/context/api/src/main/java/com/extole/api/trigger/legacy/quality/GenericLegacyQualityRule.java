package com.extole.api.trigger.legacy.quality;

public interface GenericLegacyQualityRule {
    boolean evaluate(LegacyQualityRuleTriggerContext context);
}
