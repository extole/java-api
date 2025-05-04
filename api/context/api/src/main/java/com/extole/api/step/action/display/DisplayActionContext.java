package com.extole.api.step.action.display;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.PersonContext;
import com.extole.api.RuntimeVariableContext;
import com.extole.api.campaign.VariableContext;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.step.action.StepActionContext;

@Schema
public interface DisplayActionContext
    extends StepActionContext, PersonContext, RuntimeVariableContext, VariableContext {

    InternalConsumerEventBuilder internalConsumerEventBuilder();

    boolean isMobile();

    boolean isScraper();

}
