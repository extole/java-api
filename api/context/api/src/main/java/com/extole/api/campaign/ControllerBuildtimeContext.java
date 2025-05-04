package com.extole.api.campaign;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ControllerBuildtimeContext extends CampaignBuildtimeContext {

    String getControllerName();

}
