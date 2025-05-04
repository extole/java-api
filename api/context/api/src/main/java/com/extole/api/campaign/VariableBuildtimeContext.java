package com.extole.api.campaign;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface VariableBuildtimeContext extends CampaignBuildtimeContext {

    String getName();

    String getKey();

    String getSource();

}
