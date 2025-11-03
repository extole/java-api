package com.extole.api.campaign.controller.trigger.has.prior.step;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.trigger.has.prior.step.HasPriorStepTriggerContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.RuntimeEvaluatable;

final class PartnerEventIdPojo implements PartnerEventId {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> name;
    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> value;

    @JsonCreator
    private PartnerEventIdPojo(
        @JsonProperty(JSON_NAME) RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> name,
        @JsonProperty(JSON_VALUE) RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PartnerEventIdPojo that)) {
            return false;
        }
        return Objects.equals(getName(), that.getName()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
