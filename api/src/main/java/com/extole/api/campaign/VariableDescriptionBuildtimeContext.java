package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface VariableDescriptionBuildtimeContext extends CampaignBuildtimeContext {

    String getName();

    String getSource();

    /**
     * @return the value if variable is single-key. "default" value if multi-keyed.
     */
    @Nullable
    Object getValue();

}
