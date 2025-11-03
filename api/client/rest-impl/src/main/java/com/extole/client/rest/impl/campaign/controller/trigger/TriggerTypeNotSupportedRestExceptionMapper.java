package com.extole.client.rest.impl.campaign.controller.trigger;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.controller.trigger.TriggerTypeNotSupportedException;

public enum TriggerTypeNotSupportedRestExceptionMapper {
    INSTANCE;

    public static TriggerTypeNotSupportedRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    public CampaignControllerTriggerValidationRestException map(TriggerTypeNotSupportedException exception) {
        return RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
            .withErrorCode(CampaignControllerTriggerValidationRestException.TRIGGER_TYPE_NOT_SUPPORTED)
            .addParameter("supported_trigger_types", exception.getSupportedTriggerTypes())
            .addParameter("step_type", exception.getStepType())
            .withCause(exception)
            .build();
    }
}
