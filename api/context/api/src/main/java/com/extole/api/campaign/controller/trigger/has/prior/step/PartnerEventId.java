package com.extole.api.campaign.controller.trigger.has.prior.step;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.trigger.has.prior.step.HasPriorStepTriggerContext;
import com.extole.evaluateable.RuntimeEvaluatable;

@JsonDeserialize(as = PartnerEventIdPojo.class)
@Schema
public interface PartnerEventId {

    RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> getName();

    RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<String>> getValue();

}
