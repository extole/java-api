package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ReportType extends EventEntity {
    String getId();

    String getType();

    String getName();

    String getDisplayName();

    String getDescription();

    String[] getCategories();

    String[] getScopes();

    String getVisibility();

    String[] getTags();

    String[] getFormats();

    String getDataStart();

    String[] getAllowedScopes();

}
