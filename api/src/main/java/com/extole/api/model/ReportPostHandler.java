package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ReportPostHandler extends EventEntity {

    String getId();

    String getName();

    boolean isEnabled();

    String getCreatedDate();

    String getUpdatedDate();

    ReportPostHandlerAction[] getActions();

    ReportPostHandlerCondition[] getConditions();

    interface ReportPostHandlerCondition {
        String getType();
    }

    interface ReportPostHandlerAction {
        String getType();
    }
}
