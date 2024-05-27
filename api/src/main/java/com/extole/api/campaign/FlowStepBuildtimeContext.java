package com.extole.api.campaign;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface FlowStepBuildtimeContext extends CampaignBuildtimeContext {

    String getName();

    String getStepName();
}
