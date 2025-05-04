package com.extole.api.model;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface AudienceList extends EventEntity {
    String getName();

    String getCreatedDate();

    String getUpdatedDate();

    String[] getTags();

    String getType();

    @Nullable
    String getDescription();

    String[] getEventColumns();

    Map<String, String> getEventData();
}
