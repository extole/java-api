package com.extole.api.campaign;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.service.ComponentService;

@Schema
public interface CampaignBuildtimeContext extends ComponentBuildtimeContext, GlobalContext, LoggerContext {

    String getProgramLabel();

    ComponentService getComponentService();
}
